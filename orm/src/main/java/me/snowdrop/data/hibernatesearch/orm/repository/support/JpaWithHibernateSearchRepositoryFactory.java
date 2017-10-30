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
package me.snowdrop.data.hibernatesearch.orm.repository.support;

import me.snowdrop.data.hibernatesearch.core.HibernateSearchOperations;
import me.snowdrop.data.hibernatesearch.core.HibernateSearchTemplate;
import me.snowdrop.data.hibernatesearch.orm.JpaDatasourceMapper;
import me.snowdrop.data.hibernatesearch.repository.support.HibernateSearchRepositoryFactory;
import me.snowdrop.data.hibernatesearch.spi.DatasourceMapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryComposition.RepositoryFragments;
import org.springframework.data.repository.query.EvaluationContextProvider;
import org.springframework.data.repository.query.QueryLookupStrategy;

import javax.persistence.EntityManager;

public class JpaWithHibernateSearchRepositoryFactory extends JpaRepositoryFactory {

  private final HibernateSearchRepositoryFactory hibernateSearchRepositoryFactory;

  public JpaWithHibernateSearchRepositoryFactory(EntityManager entityManager) {
    super(entityManager);
    DatasourceMapper datasourceMapper = new JpaDatasourceMapper(entityManager.getEntityManagerFactory());
    HibernateSearchOperations operations = new HibernateSearchTemplate(datasourceMapper);
    hibernateSearchRepositoryFactory = new HibernateSearchRepositoryFactory(operations);
  }

  @Override
  public void setQueryLookupStrategyKey(QueryLookupStrategy.Key key) {
    super.setQueryLookupStrategyKey(key);
    // TODO separate HSearch config from JPA config
    hibernateSearchRepositoryFactory.setQueryLookupStrategyKey(key);
  }

  @Override
  public void setNamedQueries(NamedQueries namedQueries) {
    super.setNamedQueries(namedQueries);
    // TODO separate HSearch config from JPA config
    hibernateSearchRepositoryFactory.setNamedQueries(namedQueries);
  }

  @Override
  public void setEvaluationContextProvider(EvaluationContextProvider evaluationContextProvider) {
    super.setEvaluationContextProvider(evaluationContextProvider);
    hibernateSearchRepositoryFactory.setEvaluationContextProvider(evaluationContextProvider);
  }

  @Override
  public void setBeanClassLoader(ClassLoader classLoader) {
    super.setBeanClassLoader(classLoader);
    hibernateSearchRepositoryFactory.setBeanClassLoader(classLoader);
  }

  @Override
  public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
    super.setBeanFactory(beanFactory);
    hibernateSearchRepositoryFactory.setBeanFactory(beanFactory);
  }

  @Override
  protected RepositoryFragments getRepositoryFragments(RepositoryMetadata metadata) {
    return super.getRepositoryFragments(metadata).append(getHibernateSearchFragments(metadata));
  }

  private RepositoryFragments getHibernateSearchFragments(RepositoryMetadata metadata) {
    return hibernateSearchRepositoryFactory.getExtensionFragments(metadata);
  }
}
