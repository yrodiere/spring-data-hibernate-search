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
import me.snowdrop.data.repository.extension.config.RepositoryExtensionConfigurationSource;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.env.Environment;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.repository.config.*;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Delegate for configuration integration to reuse the general way of detecting repository extensions.
 * Customization is done by providing a configuration format specific {@link RepositoryConfigurationSource}
 * (currently either XML or annotations are supported).
 * The actual registration can then be triggered for different
 * {@link me.snowdrop.data.repository.extension.config.RepositoryExtensionConfiguration}s.
 * <p>
 * Copy/pasted and adapted from {@link RepositoryExtensionConfigurationDelegate}
 * in order to customize the name of produced beans.
 *
 * @author Oliver Gierke
 * @author Yoann Rodiere
 */
public class RepositoryExtensionConfigurationDelegate {

  private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryExtensionConfigurationDelegate.class);

  private static final String REPOSITORY_EXTENSION_REGISTRATION = "Spring Data {} - Registering repository extension: {} - Interface: {} - Factory: {}";

  static final String FACTORY_BEAN_OBJECT_TYPE = "factoryBeanObjectType";

  private final RepositoryExtensionConfigurationSource configurationSource;
  private final ResourceLoader resourceLoader;
  private final Environment environment;
  private final boolean isXml;

  /**
   * Creates a new {@link RepositoryExtensionConfigurationDelegate} for the given
   * {@link RepositoryExtensionConfigurationSource} and {@link ResourceLoader} and {@link Environment}.
   *
   * @param configurationSource must not be {@literal null}.
   * @param resourceLoader      must not be {@literal null}.
   * @param environment         must not be {@literal null}.
   */
  public RepositoryExtensionConfigurationDelegate(
          RepositoryExtensionConfigurationSource configurationSource, ResourceLoader resourceLoader,
          Environment environment) {
    this.isXml = configurationSource instanceof XmlRepositoryConfigurationSource;
    boolean isAnnotation = configurationSource instanceof AnnotationRepositoryConfigurationSource;

    Assert.isTrue(isXml || isAnnotation,
            "Configuration source must either be an Xml- or an AnnotationBasedConfigurationSource!");
    Assert.notNull(resourceLoader, "ResourceLoader must not be null!");

    RepositoryBeanNameGenerator generator = new RepositoryBeanNameGenerator();
    generator.setBeanClassLoader(resourceLoader.getClassLoader());

    this.configurationSource = configurationSource;
    this.resourceLoader = resourceLoader;
    this.environment = defaultEnvironment(environment, resourceLoader);
  }

  /**
   * Defaults the environment in case the given one is null. Used as fallback, in case the legacy constructor was
   * invoked.
   *
   * @param environment    can be {@literal null}.
   * @param resourceLoader can be {@literal null}.
   * @return
   */
  private static Environment defaultEnvironment(Environment environment, ResourceLoader resourceLoader) {
    if (environment != null) {
      return environment;
    }

    return resourceLoader instanceof EnvironmentCapable ? ((EnvironmentCapable) resourceLoader).getEnvironment()
            : new StandardEnvironment();
  }

  /**
   * Registers the found repositories in the given {@link BeanDefinitionRegistry}.
   *
   * @param registry
   * @param extension
   * @return {@link BeanComponentDefinition}s for all repository bean definitions found.
   */
  public List<BeanComponentDefinition> registerRepositoriesIn(
          BeanDefinitionRegistry registry, ExtendingRepositoryConfigurationExtension extension) {
    extension.registerBeansForRoot(registry, configurationSource);

    RepositoryBeanDefinitionBuilder builder = new RepositoryBeanDefinitionBuilder(
            registry, extension, resourceLoader, environment);
    List<BeanComponentDefinition> definitions = new ArrayList<BeanComponentDefinition>();

    for (RepositoryExtensionConfiguration<? extends RepositoryConfigurationSource> configuration :
            extension.getRepositoryExtensionConfigurations(configurationSource, resourceLoader)) {
      RepositoryConfiguration<?> extendingRepositoryConfiguration = configuration.getExtendingRepositoryConfiguration();
      BeanDefinitionBuilder definitionBuilder = builder.build(extendingRepositoryConfiguration);

      extension.postProcess(definitionBuilder, configurationSource);

      if (isXml) {
        extension.postProcess(definitionBuilder, (XmlRepositoryConfigurationSource) configurationSource);
      } else {
        extension.postProcess(definitionBuilder, (AnnotationRepositoryConfigurationSource) configurationSource);
      }

      AbstractBeanDefinition beanDefinition = definitionBuilder.getBeanDefinition();
      String extendingRepositoryImplementationClassName =
              ClassUtils.getShortName(configuration.getExtendedRepositoryInterface())
                      + configurationSource.getExtendedRepositoryImplementationPostfix();
      String beanName = StringUtils.uncapitalize( extendingRepositoryImplementationClassName );

      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug(REPOSITORY_EXTENSION_REGISTRATION, extension.getModuleName(), beanName,
                extendingRepositoryConfiguration.getRepositoryInterface(), extension.getRepositoryFactoryClassName());
      }

      beanDefinition.setAttribute(FACTORY_BEAN_OBJECT_TYPE, extendingRepositoryConfiguration.getRepositoryInterface());

      registry.registerBeanDefinition(beanName, beanDefinition);
      definitions.add(new BeanComponentDefinition(beanDefinition, beanName));
    }

    return definitions;
  }
}
