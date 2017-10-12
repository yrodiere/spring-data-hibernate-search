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

import org.springframework.core.io.ResourceLoader;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;

import java.util.Collection;

public interface ExtendingRepositoryConfigurationExtension extends RepositoryConfigurationExtension {

  /**
   * Returns all {@link RepositoryExtensionConfiguration}s for extension candidates obtained through
   * the given {@link ExtendingRepositoryConfigurationSource}.
   *
   * @param configSource
   * @param loader
   * @return
   */
  <T extends ExtendingRepositoryConfigurationSource>
  Collection<RepositoryExtensionConfiguration<?>> getRepositoryExtensionConfigurations(
          T configSource, ResourceLoader loader);

}
