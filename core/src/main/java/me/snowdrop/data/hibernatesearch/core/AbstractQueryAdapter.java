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

package me.snowdrop.data.hibernatesearch.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.snowdrop.data.hibernatesearch.spi.QueryAdapter;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.hibernate.search.query.engine.spi.EntityInfo;
import org.hibernate.search.query.engine.spi.HSQuery;
import org.hibernate.search.spi.SearchIntegrator;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public abstract class AbstractQueryAdapter<T> implements QueryAdapter<T> {
  private final Class<T> entityClass;
  private HSQuery hsQuery;

  public AbstractQueryAdapter(Class<T> entityClass) {
    this.entityClass = entityClass;
  }

  protected abstract T get(Class<T> entityClass, Serializable id);

  public void applyLuceneQuery(SearchIntegrator searchIntegrator, Query query) {
    hsQuery = searchIntegrator
      .createHSQuery()
      .luceneQuery(query)
      .targetedEntities(Collections.singletonList(entityClass));
  }

  @Override
  public long size() {
    return hsQuery.queryResultSize();
  }

  public List<T> list() {
    List<T> list = new ArrayList<>();
    for (EntityInfo ei : hsQuery.queryEntityInfos()) {
      list.add(get(entityClass, ei.getId()));
    }
    return list;
  }

  @Override
  public void setSort(Sort sort) {
    hsQuery.sort(sort);
  }

  @Override
  public void setFirstResult(int firstResult) {
    hsQuery.firstResult(firstResult);
  }

  @Override
  public void setMaxResults(int maxResults) {
    hsQuery.maxResults(maxResults);
  }
}