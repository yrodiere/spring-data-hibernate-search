/*
 * Copyright 2014-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.snowdrop.data.repository.extension.support;

import me.snowdrop.data.repository.extension.config.ExtendingRepositoryConfigurationExtension;
import me.snowdrop.data.repository.extension.config.RepositoryExtensionConfiguration;
import me.snowdrop.data.repository.extension.config.ExtendingRepositoryConfigurationSource;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.repository.config.*;
import org.springframework.util.ClassUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RepositoryExtensionConfigurationDelegate {

  private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryExtensionConfigurationDelegate.class);

  private static final String REPOSITORY_EXTENSION_REGISTRATION = "Spring Data {} - Registering repository extension: {} - Interface: {} - Factory: {} - Extended interface: {}";

  static final String FACTORY_BEAN_OBJECT_TYPE = "factoryBeanObjectType";

  private final ExtendingRepositoryConfigurationSource configurationSource;
  private final ResourceLoader resourceLoader;
  private final RepositoryConfigurationDelegate delegate;

  public RepositoryExtensionConfigurationDelegate(
          ExtendingRepositoryConfigurationSource configurationSource, ResourceLoader resourceLoader,
          Environment environment) {
    this.configurationSource = configurationSource;
    this.resourceLoader = resourceLoader;
    this.delegate = new RepositoryConfigurationDelegate(configurationSource, resourceLoader, environment);
  }

  /**
   * Registers the found repositories and repository extensions in the given {@link BeanDefinitionRegistry}.
   *
   * @param registry
   * @param extension
   * @return {@link BeanComponentDefinition}s for all repository bean definitions found.
   */
  public List<BeanComponentDefinition> registerRepositoriesAndExtensionsIn(
          BeanDefinitionRegistry registry, ExtendingRepositoryConfigurationExtension extension) {
    List<BeanComponentDefinition> originalDefinitions = delegate.registerRepositoriesIn(registry, extension);
    Map<String, BeanComponentDefinition> definitionsByInterfaceName = originalDefinitions.stream()
            .collect(Collectors.toMap(
                    def -> (String) def.getBeanDefinition().getAttribute(FACTORY_BEAN_OBJECT_TYPE),
                    Function.identity()
            ));
    List<BeanComponentDefinition> overriddenDefinitions = new ArrayList<>();

    for (RepositoryExtensionConfiguration<?> extensionConfiguration :
            extension.getRepositoryExtensionConfigurations(configurationSource, resourceLoader)) {
      Class<?> repositoryExtensionInterface = extensionConfiguration.getRepositoryExtensionInterface();
      BeanComponentDefinition originalExtensionDefinition =
              definitionsByInterfaceName.get(repositoryExtensionInterface.getName());
      if (originalExtensionDefinition == null) {
        // FIXME debug log or error
        continue;
      }

      Class<?> extendedRepositoryInterface = extensionConfiguration.getExtendedRepositoryInterface();
      String capitalizedBeanName = ClassUtils.getShortName(extendedRepositoryInterface)
              + extensionConfiguration.getConfigurationSource().getExtensionImplementationPostfix();
      String overriddenBeanName = StringUtils.uncapitalize(capitalizedBeanName);

      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug(REPOSITORY_EXTENSION_REGISTRATION, extension.getModuleName(), overriddenBeanName,
                repositoryExtensionInterface, extension.getRepositoryFactoryClassName(),
                extendedRepositoryInterface);
      }

      String originalBeanName = originalExtensionDefinition.getName();
      if (registry.containsBeanDefinition(originalBeanName)) {
        registry.removeBeanDefinition(originalBeanName);
      }

      BeanDefinition extensionInternalDefinition = originalExtensionDefinition.getBeanDefinition();
      registry.registerBeanDefinition(overriddenBeanName, extensionInternalDefinition);

      BeanComponentDefinition overriddenComponentDefinition =
              new BeanComponentDefinition(extensionInternalDefinition, overriddenBeanName);
      overriddenDefinitions.add(overriddenComponentDefinition);
    }

    return overriddenDefinitions;
  }
}
