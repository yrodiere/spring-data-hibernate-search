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
package me.snowdrop.data.hibernatesearch.config.jpa.extension.smoke;

import me.snowdrop.data.hibernatesearch.config.HibernateSearchDataInfinispanAutoConfiguration;
import me.snowdrop.data.hibernatesearch.config.HibernateSearchDataJpaAutoConfiguration;
import me.snowdrop.data.hibernatesearch.config.jpa.JpaConfiguration;
import me.snowdrop.data.hibernatesearch.config.jpa.extension.smoke.repository.jpa.ExtendedJpaFruitRepository;
import me.snowdrop.data.hibernatesearch.orm.repository.support.JpaWithHibernateSearchRepositoryFactoryBean;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@Import(JpaConfiguration.class)
@EnableAutoConfiguration(exclude = {
        HibernateSearchDataInfinispanAutoConfiguration.class,
        HibernateSearchDataJpaAutoConfiguration.class // Not needed in this case
})
@EnableJpaRepositories(
        basePackageClasses = ExtendedJpaFruitRepository.class,
        repositoryFactoryBeanClass = JpaWithHibernateSearchRepositoryFactoryBean.class
)
public class ExtendedJpaSmokeConfiguration {

}
