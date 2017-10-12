/*
 * Copyright 2012-2017 the original author or authors.
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

import me.snowdrop.data.repository.extension.config.AnnotationRepositoryExtensionConfigurationSource;
import me.snowdrop.data.repository.extension.config.ExtendingRepositoryConfigurationExtension;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.repository.config.*;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;

/**
 * Base class to implement {@link ImportBeanDefinitionRegistrar}s to enable repository extensions.
 * <p>
 * Copy/pasted and adapted from {@link RepositoryBeanDefinitionRegistrarSupport}
 * in order to use {@link RepositoryExtensionConfigurationDelegate} instead of {@link RepositoryConfigurationDelegate}
 * and {@link me.snowdrop.data.repository.extension.config.AnnotationRepositoryExtensionConfigurationSource}
 * instead of {@link AnnotationRepositoryConfigurationSource}.
 *
 * @author Oliver Gierke
 * @author Yoann Rodiere
 */
public abstract class RepositoryExtensionBeanDefinitionRegistrarSupport
        implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {

  private ResourceLoader resourceLoader;
  private Environment environment;

  @Override
  public void setResourceLoader(ResourceLoader resourceLoader) {
    this.resourceLoader = resourceLoader;
  }

  @Override
  public void setEnvironment(Environment environment) {
    this.environment = environment;
  }

  public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
    Assert.notNull(resourceLoader, "ResourceLoader must not be null!");
    Assert.notNull(annotationMetadata, "AnnotationMetadata must not be null!");
    Assert.notNull(registry, "BeanDefinitionRegistry must not be null!");

    // Guard against calls for sub-classes
    if (annotationMetadata.getAnnotationAttributes(getAnnotation().getName()) == null) {
      return;
    }

    AnnotationRepositoryExtensionConfigurationSource configurationSource = new AnnotationRepositoryExtensionConfigurationSource(
            annotationMetadata, getAnnotation(), resourceLoader, environment, registry);

    ExtendingRepositoryConfigurationExtension extension = getExtension();
    // Do NOT expose the registration so as not to confuse extensions with actual repositories
    // TODO: expose the extension registration some other way?
    //RepositoryConfigurationUtils.exposeRegistration(extension, registry, configurationSource);

    RepositoryExtensionConfigurationDelegate delegate = new RepositoryExtensionConfigurationDelegate(
            configurationSource, resourceLoader, environment);

    delegate.registerRepositoriesIn(registry, extension);
  }

  /**
   * Return the annotation to obtain configuration information from. Will be wrapped into an
   * {@link AnnotationRepositoryConfigurationSource} so have a look at the constants in there for what annotation
   * attributes it expects.
   *
   * @return
   */
  protected abstract Class<? extends Annotation> getAnnotation();

  /**
   * Returns the {@link ExtendingRepositoryConfigurationExtension} for store specific callbacks and {@link BeanDefinition}
   * post-processing.
   *
   * @return
   * @see me.snowdrop.data.repository.extension.config.ExtendingRepositoryConfigurationExtensionSupport
   */
  protected abstract ExtendingRepositoryConfigurationExtension getExtension();
}
