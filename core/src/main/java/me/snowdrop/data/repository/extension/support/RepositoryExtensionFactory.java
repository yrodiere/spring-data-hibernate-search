/*
 * Copyright 2008-2015 the original author or authors.
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

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

public class RepositoryExtensionFactory implements BeanClassLoaderAware {

  private ClassLoader classLoader = org.springframework.util.ClassUtils.getDefaultClassLoader();

  @Override
  public void setBeanClassLoader(ClassLoader classLoader) {
    this.classLoader = classLoader == null ? org.springframework.util.ClassUtils.getDefaultClassLoader() : classLoader;
  }

  public <T> T getRepositoryExtension(Class<T> repositoryInterface, Object extendingRepository) {
    return getRepositoryExtension(repositoryInterface, extendingRepository, null);
  }

  public <T> T getRepositoryExtension(Class<T> repositoryExtensionInterface, Object extendingRepository, Object customImplementation) {
    // Create proxy
    ProxyFactory result = new ProxyFactory();
    result.setTarget(extendingRepository);
    result.setInterfaces(repositoryExtensionInterface);

    return (T) result.getProxy(classLoader);
  }

}
