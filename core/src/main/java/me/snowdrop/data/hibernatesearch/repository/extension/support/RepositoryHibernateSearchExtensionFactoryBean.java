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
package me.snowdrop.data.hibernatesearch.repository.extension.support;

import me.snowdrop.data.hibernatesearch.core.HibernateSearchOperations;
import me.snowdrop.data.hibernatesearch.core.HibernateSearchTemplate;
import me.snowdrop.data.hibernatesearch.repository.extension.RepositoryHibernateSearchExtension;
import me.snowdrop.data.hibernatesearch.repository.support.HibernateSearchRepositoryFactory;
import me.snowdrop.data.hibernatesearch.spi.DatasourceMapper;
import me.snowdrop.data.repository.extension.support.RepositoryExtensionFactoryBeanSupport;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.util.Assert;

import java.io.Serializable;

public class RepositoryHibernateSearchExtensionFactoryBean<T extends RepositoryHibernateSearchExtension<S, ID>, S, ID extends Serializable>
        extends RepositoryExtensionFactoryBeanSupport<T, S, ID> {

  private DatasourceMapper datasourceMapper;
  private HibernateSearchOperations hibernateSearchOperations;

  public RepositoryHibernateSearchExtensionFactoryBean(Class<? extends T> repositoryInterface) {
    super(repositoryInterface);
  }

  public void setDatasourceMapper(DatasourceMapper datasourceMapper) {
    this.datasourceMapper = datasourceMapper;
  }

  public void setHibernateSearchOperations(HibernateSearchOperations hibernateSearchOperations) {
    this.hibernateSearchOperations = hibernateSearchOperations;
  }

  @Override
  public void afterPropertiesSet() {
    if (hibernateSearchOperations == null) {
      Assert.notNull(datasourceMapper, "DatasourceMapper must be configured!");

      hibernateSearchOperations = new HibernateSearchTemplate(datasourceMapper);
    }

    setMappingContext(hibernateSearchOperations.getMappingContext());

    super.afterPropertiesSet();
  }

  @Override
  protected RepositoryFactorySupport createRepositoryFactory() {
    return new HibernateSearchRepositoryFactory(hibernateSearchOperations);
  }
}
