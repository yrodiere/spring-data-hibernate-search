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

import me.snowdrop.data.repository.extension.util.OverridingAnnotationMetadataWrapper;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.repository.config.AnnotationRepositoryConfigurationSource;

import java.lang.annotation.Annotation;
import java.util.Optional;

public class AnnotationExtendedRepositoryConfigurationSource implements ExtendedRepositoryConfigurationSource {

  private final AnnotationRepositoryConfigurationSource delegate;

  public AnnotationExtendedRepositoryConfigurationSource(
          AnnotationMetadata rootMetadata, Class<? extends Annotation> extendAnnotationType,
          AnnotationAttributes attributes,
          ResourceLoader loader, Environment environment, BeanDefinitionRegistry registry) {
    AnnotationMetadata metadata = new OverridingAnnotationMetadataWrapper(
            rootMetadata, extendAnnotationType.getName(), attributes);
    this.delegate = new AnnotationRepositoryConfigurationSource(
            metadata, extendAnnotationType, loader, environment, registry);
  }

  @Override
  public Optional<String> getExtensionImplementationPostfix() {
    return delegate.getRepositoryImplementationPostfix();
  }

  @Override
  public Iterable<BeanDefinition> getCandidatesToExtension(ResourceLoader loader) {
    return delegate.getCandidates(loader);
  }
}
