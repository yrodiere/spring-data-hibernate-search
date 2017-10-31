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

package me.snowdrop.data.hibernatesearch.ops;

import me.snowdrop.data.hibernatesearch.DatasourceMapperForTest;
import me.snowdrop.data.hibernatesearch.TestUtils;
import me.snowdrop.data.hibernatesearch.TestedRepository;
import me.snowdrop.data.hibernatesearch.TestsAction;
import me.snowdrop.data.hibernatesearch.repository.config.EnableHibernateSearchRepositories;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@Configuration
@EnableHibernateSearchRepositories
public class OpsConfiguration {
  @Bean(destroyMethod = "close")
  public DatasourceMapperForTest datasourceMapper() {
    return TestUtils.createDatasourceMapper(SimpleEntity.class);
  }

  @Bean
  public TestsAction testsAction(DatasourceMapperForTest datasourceMapper) {
    return new OpsTestsAction(datasourceMapper);
  }

  @Bean
  public TestedRepository<Ops> testedRepository(OpsRepository repository) {
    return new TestedRepository<>(repository);
  }
}
