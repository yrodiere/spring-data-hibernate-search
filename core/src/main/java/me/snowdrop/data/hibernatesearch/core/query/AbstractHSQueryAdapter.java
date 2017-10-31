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

package me.snowdrop.data.hibernatesearch.core.query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import me.snowdrop.data.hibernatesearch.util.Integers;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.hibernate.search.query.engine.spi.EntityInfo;
import org.hibernate.search.query.engine.spi.HSQuery;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public abstract class AbstractHSQueryAdapter<T> extends AbstractQueryAdapter<T> {
  private HSQuery hsQuery;

  protected abstract T get(Class<T> entityClass, Serializable id);

  protected long size() {
    return hsQuery.queryResultSize();
  }

  protected List<T> list() {
    List<T> list = new ArrayList<>();
    for (EntityInfo ei : hsQuery.queryEntityInfos()) {
      list.add(get(entityClass, ei.getId()));
    }
    return list;
  }

  protected Stream<T> stream() {
    return toStream(list());
  }

  protected void applyLuceneQuery(Query query) {
    hsQuery = getSearchIntegrator().createHSQuery(query, entityClass);
  }

  protected void setSort(Sort sort) {
    hsQuery.sort(sort);
  }

  protected void setFirstResult(long firstResult) {
    hsQuery.firstResult(Integers.safeCast(firstResult));
  }

  protected void setMaxResults(long maxResults) {
    hsQuery.maxResults(Integers.safeCast(maxResults));
  }
}