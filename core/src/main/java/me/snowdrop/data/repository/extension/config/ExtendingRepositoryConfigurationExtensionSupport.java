/*
 * Copyright 2017 Red Hat, Inc, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.snowdrop.data.repository.extension.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.repository.config.RepositoryConfiguration;
import org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport;
import org.springframework.data.repository.config.RepositoryConfigurationSource;
import org.springframework.util.Assert;

import java.util.*;

public abstract class ExtendingRepositoryConfigurationExtensionSupport extends RepositoryConfigurationExtensionSupport
        implements ExtendingRepositoryConfigurationExtension {
  private static final Logger LOGGER = LoggerFactory.getLogger(ExtendingRepositoryConfigurationExtensionSupport.class);
  private static final String EXTENSION_SCANNING_CLASS_LOADING_ERROR = "%s - Extension scanning - Could not load type %s using class loader %s.";
  private static final String EXTENSION_CANDIDATE_DROPPED = "%s - Extension scanning - Type %s does not implement any extension interface.";

  @Override
  public <T extends RepositoryConfigurationSource> Collection<RepositoryExtensionConfiguration<T>> getRepositoryExtensionConfigurations(
          T configSource, ResourceLoader loader) {
    Assert.notNull(configSource, "ConfigSource must not be null!");
    Assert.notNull(loader, "Loader must not be null!");

    Set<RepositoryExtensionConfiguration<T>> result = new HashSet<>();

    for (BeanDefinition extensionCandidate : configSource.getCandidates(loader)) {
      Class<?> extensionCandidateRepositoryInterface = loadRepositoryInterface(extensionCandidate, loader);

      if (extensionCandidateRepositoryInterface != null) {
        List<Class<?>> extensionInterfaces = getRepositoryExtensionInterfaces( extensionCandidateRepositoryInterface );
        for (Class<?> extensionInterface : extensionInterfaces) {
          RepositoryExtensionConfiguration<T> configuration = getRepositoryExtensionConfiguration(
                  extensionCandidateRepositoryInterface, extensionInterface, configSource );
          result.add(configuration);
        }
      }
    }

    return result;
  }

  /**
   * Returns the types that indicate a store match when inspecting repository extensions.
   *
   * @return
   */
  protected abstract Collection<Class<?>> getIdentifyingExtensionTypes();

  private List<Class<?>> getRepositoryExtensionInterfaces(Class<?> repositoryInterface) {
    List<Class<?>> result = new ArrayList<>();
    Collection<Class<?>> identifyingTypes = getIdentifyingExtensionTypes();

    for (Class<?> extendedInterface : repositoryInterface.getInterfaces()) {
      for (Class<?> identifyingType : identifyingTypes) {
        if (identifyingType.isAssignableFrom(extendedInterface)) {
          result.add(extendedInterface);
        }
      }
    }

    if (result.isEmpty()) {
      LOGGER.info(EXTENSION_CANDIDATE_DROPPED, getModuleName(), repositoryInterface);
    }

    return result;
  }

  private <T extends RepositoryConfigurationSource> RepositoryExtensionConfiguration<T> getRepositoryExtensionConfiguration(
          Class<?> extendedRepositoryInterface, Class<?> extensionInterface, T configSource) {
    BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(extensionInterface);
    AbstractBeanDefinition extendingRepositoryBeanDefinition = builder.getBeanDefinition();
    RepositoryConfiguration<T> extendingRepositoryConfig =
            getRepositoryConfiguration( extendingRepositoryBeanDefinition, configSource );
    return new DefaultRepositoryExtensionConfiguration<>( extendedRepositoryInterface, extendingRepositoryConfig );
  }

  private Class<?> loadRepositoryInterface(BeanDefinition extensionCandidate, ResourceLoader loader) {
    String repositoryInterface = extensionCandidate.getBeanClassName();
    ClassLoader classLoader = loader.getClassLoader();

    try {
      return org.springframework.util.ClassUtils.forName(repositoryInterface, classLoader);
    } catch (ClassNotFoundException|LinkageError e) {
      LOGGER.warn(String.format(EXTENSION_SCANNING_CLASS_LOADING_ERROR, getModuleName(), repositoryInterface, classLoader), e);
    }

    return null;
  }
}
