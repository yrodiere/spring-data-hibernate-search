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

import me.snowdrop.data.hibernatesearch.TestUtils;
import me.snowdrop.data.hibernatesearch.config.HibernateSearchDataInfinispanAutoConfiguration;
import me.snowdrop.data.hibernatesearch.config.HibernateSearchDataJpaAutoConfiguration;
import me.snowdrop.data.hibernatesearch.config.smoke.jpa.JpaConfiguration;
import me.snowdrop.data.hibernatesearch.config.smoke.Fruit;
import me.snowdrop.data.hibernatesearch.config.smoke.repository.extension.jpa.FruitExtendedJpaRepository;
import me.snowdrop.data.hibernatesearch.orm.repository.config.JpaWithHibernateSearchConfiguration;
import me.snowdrop.data.hibernatesearch.orm.repository.support.JpaWithHibernateSearchRepositoryFactoryBean;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.Repository;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@SpringBootTest(classes = JpaConfiguration.class, properties = "debug=false")
@RunWith(SpringRunner.class)
@EnableAutoConfiguration(exclude = {
        HibernateSearchDataInfinispanAutoConfiguration.class,
        HibernateSearchDataJpaAutoConfiguration.class // Not needed in this case
})
@EnableJpaRepositories(
        basePackageClasses = FruitExtendedJpaRepository.class,
        repositoryFactoryBeanClass = JpaWithHibernateSearchRepositoryFactoryBean.class
)
public class ExtendedJpaTests {
  /*
   * This makes sure that there is only one matching repository implementation
   * (which seems to be necessary for the Spring Data REST integration to work properly)
   */
  @Autowired
  Repository<Fruit, Long> singleRepositoryImplementation;

  @Autowired
  FruitExtendedJpaRepository jpaRepository;

  @Test
  public void testDefault() {
    Assert.assertNotNull(jpaRepository);

    Assert.assertEquals(3, jpaRepository.count());

    Assert.assertEquals(3, TestUtils.size(jpaRepository.findAll()));

    // Ask for a lowercase match, which would only work with Hibernate Search, not with the JPQL 'equals'
    Fruit apple = jpaRepository.findByName("apple");
    Assert.assertNotNull(apple);
    Assert.assertEquals("Apple", apple.getName());
  }
}
