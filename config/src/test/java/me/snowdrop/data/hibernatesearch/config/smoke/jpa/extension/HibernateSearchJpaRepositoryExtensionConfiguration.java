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

import me.snowdrop.data.hibernatesearch.config.smoke.Fruit;
import me.snowdrop.data.hibernatesearch.config.smoke.repository.extension.hibernatesearch.FruitExtendingJpaHibernateSearchRepository;
import me.snowdrop.data.hibernatesearch.config.smoke.repository.extension.hibernatesearch.FruitHibernateSearchRepositoryExtension;
import me.snowdrop.data.hibernatesearch.repository.config.EnableHibernateSearchRepositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableHibernateSearchRepositories(basePackageClasses = FruitHibernateSearchRepositoryExtension.class)
public class HibernateSearchJpaRepositoryExtensionConfiguration {

  @Autowired
  private FruitExtendingJpaHibernateSearchRepository fruit;

  @Bean("fruitExtendedJpaRepositoryImpl") // CAUTION: name MUST be the one of the extended JPA interface + "Impl"
  public FruitHibernateSearchRepositoryExtension fruitExtension() throws Exception {
    // TODO use proxies instead of an anonymous class
    // TODO do not require that users also define a separate HibernateSearchRepository
    // TODO detect the required bean name automatically
    // TODO execute this code without the need for a user @Configuration (just with @EnableHibernateSearchXXX), and make sure to initialize eagerly (so that implementations are detected by JPA)
    return new FruitHibernateSearchRepositoryExtension() {
      @Override
      public Fruit findByName(String name) {
        return fruit.findByName( name );
      }
    };
  }

}
