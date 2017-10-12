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

package me.snowdrop.data.hibernatesearch.config.smoke.jpa.extension;

import me.snowdrop.data.hibernatesearch.config.smoke.repository.extension.hibernatesearch.FruitRepositoryHibernateSearchExtension;
import me.snowdrop.data.hibernatesearch.config.smoke.repository.extension.jpa.FruitExtendedJpaRepository;
import me.snowdrop.data.hibernatesearch.repository.config.EnableHibernateSearchRepositories;
import me.snowdrop.data.hibernatesearch.repository.config.EnableHibernateSearchRepositories.Extend;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableHibernateSearchRepositories(
        basePackageClasses = FruitRepositoryHibernateSearchExtension.class,
        extend = @Extend(basePackageClasses = FruitExtendedJpaRepository.class)
)
public class HibernateSearchJpaRepositoryExtensionConfiguration {

}
