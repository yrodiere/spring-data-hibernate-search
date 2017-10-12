/*
 * Copyright 2008-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.snowdrop.data.repository.extension.support;

import me.snowdrop.data.repository.extension.RepositoryExtension;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.core.support.EventPublishingRepositoryProxyPostProcessor;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.query.DefaultEvaluationContextProvider;
import org.springframework.data.repository.query.EvaluationContextProvider;
import org.springframework.data.repository.query.QueryLookupStrategy.Key;
import org.springframework.util.Assert;

import java.io.Serializable;



/**
 * Adapter for Springs {@link FactoryBean} interface to allow easy setup of repository extension factories
 * via Spring configuration.
 * <p>
 * Copy/pasted and adapted from {@link org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport}.
 *
 * @param <T> the type of the repository
 * @author Oliver Gierke
 * @author Thomas Darimont
 * @author Yoann Rodiere
 */
public abstract class RepositoryExtensionFactoryBeanSupport<T extends RepositoryExtension<S, ID>, S, ID extends Serializable>
        implements InitializingBean, FactoryBean<T>, BeanClassLoaderAware,
        BeanFactoryAware, ApplicationEventPublisherAware {

  private final Class<? extends T> repositoryExtensionInterface;

  private RepositoryFactorySupport repositoryFactory;
  private RepositoryExtensionFactory repositoryExtensionFactory;
  private Key queryLookupStrategyKey;
  private Class<?> repositoryExtensionBaseClass;
  private Object customImplementation;
  private NamedQueries namedQueries;
  private MappingContext<?, ?> mappingContext;
  private ClassLoader classLoader;
  private BeanFactory beanFactory;
  private boolean lazyInit = false;
  private EvaluationContextProvider evaluationContextProvider = DefaultEvaluationContextProvider.INSTANCE;
  private ApplicationEventPublisher publisher;

  private T repositoryExtension;

  protected RepositoryExtensionFactoryBeanSupport(Class<? extends T> repositoryExtensionInterface) {
    Assert.notNull(repositoryExtensionInterface, "Repository extension interface must not be null!");
    this.repositoryExtensionInterface = repositoryExtensionInterface;
  }

  /**
   * Configures the repository extension base class to be used.
   *
   * @param repositoryExtensionBaseClass the repositoryExtensionBaseClass to set, can be {@literal null}.
   * @since 1.11
   */
  public void setRepositoryExtensionBaseClass(Class<?> repositoryExtensionBaseClass) {
    this.repositoryExtensionBaseClass = repositoryExtensionBaseClass;
  }

  public void setQueryLookupStrategyKey(Key queryLookupStrategyKey) {
    this.queryLookupStrategyKey = queryLookupStrategyKey;
  }

  public void setCustomImplementation(Object customImplementation) {
    this.customImplementation = customImplementation;
  }

  public void setNamedQueries(NamedQueries namedQueries) {
    this.namedQueries = namedQueries;
  }

  /**
   * Configures the {@link MappingContext} to be used to lookup {@link PersistentEntity} instances for
   * {@link #getPersistentEntity()}.
   *
   * @param mappingContext
   */
  protected void setMappingContext(MappingContext<?, ?> mappingContext) {
    this.mappingContext = mappingContext;
  }

  /**
   * Sets the {@link EvaluationContextProvider} to be used to evaluate SpEL expressions in manually defined queries.
   *
   * @param evaluationContextProvider can be {@literal null}, defaults to
   *                                  {@link DefaultEvaluationContextProvider#INSTANCE}.
   */
  public void setEvaluationContextProvider(EvaluationContextProvider evaluationContextProvider) {
    this.evaluationContextProvider = evaluationContextProvider == null ? DefaultEvaluationContextProvider.INSTANCE
            : evaluationContextProvider;
  }

  /**
   * Configures whether to initialize the repositoryExtension proxy lazily.
   *
   * @param lazyInit whether to initialize the repositoryExtension proxy lazily. This defaults to {@literal false}.
   */
  public void setLazyInit(boolean lazyInit) {
    this.lazyInit = lazyInit;
  }

  @Override
  public void setBeanClassLoader(ClassLoader classLoader) {
    this.classLoader = classLoader;
  }

  @Override
  public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
    this.beanFactory = beanFactory;
  }

  @Override
  public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
    this.publisher = publisher;
  }

  public T getObject() {
    return initAndReturn();
  }

  public Class<? extends T> getObjectType() {
    return repositoryExtensionInterface;
  }

  public boolean isSingleton() {
    return true;
  }

  public void afterPropertiesSet() {
    this.repositoryFactory = createRepositoryFactory();
    this.repositoryFactory.setQueryLookupStrategyKey(queryLookupStrategyKey);
    this.repositoryFactory.setNamedQueries(namedQueries);
    this.repositoryFactory.setEvaluationContextProvider(evaluationContextProvider);
    this.repositoryFactory.setRepositoryBaseClass(repositoryExtensionBaseClass);
    this.repositoryFactory.setBeanClassLoader(classLoader);
    this.repositoryFactory.setBeanFactory(beanFactory);

    if (publisher != null) {
      this.repositoryFactory.addRepositoryProxyPostProcessor(new EventPublishingRepositoryProxyPostProcessor(publisher));
    }

    this.repositoryExtensionFactory = new RepositoryExtensionFactory();

    if (!lazyInit) {
      initAndReturn();
    }
  }

  /**
   * Returns the previously initialized repositoryExtension proxy or creates and returns the proxy if previously uninitialized.
   *
   * @return
   */
  private T initAndReturn() {
    Assert.notNull(repositoryExtensionInterface, "Repository interface must not be null on initialization!");

    if (this.repositoryExtension == null) {
      T repository = this.repositoryFactory.getRepository(repositoryExtensionInterface, customImplementation);
      this.repositoryExtension = repositoryExtensionFactory.getRepositoryExtension(
              repositoryExtensionInterface, repository);
    }

    return this.repositoryExtension;
  }

  protected abstract RepositoryFactorySupport createRepositoryFactory();
}
