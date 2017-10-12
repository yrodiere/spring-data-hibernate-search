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

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.repository.config.AnnotationRepositoryConfigurationSource;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AnnotationExtendingRepositoryConfigurationSource extends AnnotationRepositoryConfigurationSource
        implements ExtendingRepositoryConfigurationSource {

  private static final String EXTEND = "extend";

  private final AnnotationMetadata rootMetadata;
  private final Environment environment;
  private final BeanDefinitionRegistry registry;
  private final Class<? extends Annotation> extendAnnotationType;

  public AnnotationExtendingRepositoryConfigurationSource(
          AnnotationMetadata rootMetadata, Class<? extends Annotation> annotationType,
          Class<? extends Annotation> extendAnnotationType,
          ResourceLoader resourceLoader, Environment environment, BeanDefinitionRegistry registry) {
    super(rootMetadata, annotationType, resourceLoader, environment, registry);
    this.rootMetadata = rootMetadata;
    this.extendAnnotationType = extendAnnotationType;
    this.environment = environment;
    this.registry = registry;
  }

  @Override
  public Collection<? extends AnnotationExtendedRepositoryConfigurationSource> getExtendedRepositorySources(ResourceLoader resourceLoader) {
    List<AnnotationExtendedRepositoryConfigurationSource> result = new ArrayList<>();
    AnnotationAttributes[] extendAnnotations = getAttributes().getAnnotationArray(EXTEND);
    for (AnnotationAttributes attributes : extendAnnotations) {
      result.add( new AnnotationExtendedRepositoryConfigurationSource(
              rootMetadata, extendAnnotationType, attributes, resourceLoader, environment, registry) );
    }
    return result;
  }

}
