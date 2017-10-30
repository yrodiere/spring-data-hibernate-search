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

package me.snowdrop.data.hibernatesearch.repository.extension.config;

import me.snowdrop.data.hibernatesearch.repository.config.HibernateSearchRepositoryConfigExtension;
import me.snowdrop.data.repository.extension.config.ExtendingRepositoryConfigurationExtension;
import me.snowdrop.data.repository.extension.support.RepositoryExtensionBeanDefinitionRegistrarSupport;
import org.springframework.data.repository.config.RepositoryBeanDefinitionRegistrarSupport;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;

import java.lang.annotation.Annotation;

public class RepositoryHibernateSearchExtensionsRegistrar extends RepositoryExtensionBeanDefinitionRegistrarSupport {

  @Override
  protected Class<? extends Annotation> getAnnotation() {
    return EnableRepositoryHibernateSearchExtensions.class;
  }

  @Override
  protected ExtendingRepositoryConfigurationExtension getExtension() {
    return new HibernateSearchRepositoryConfigExtension();
  }

}
