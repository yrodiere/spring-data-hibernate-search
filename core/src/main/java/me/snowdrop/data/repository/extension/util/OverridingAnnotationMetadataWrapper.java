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
package me.snowdrop.data.repository.extension.util;

import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;

public class OverridingAnnotationMetadataWrapper implements AnnotationMetadata {

  private final AnnotationMetadata delegate;

  private final String overriddenAnnotationName;

  private final AnnotationAttributes overriddenAnnotationAttributes;

  public OverridingAnnotationMetadataWrapper(
          AnnotationMetadata delegate, String overriddenAnnotationName,
          AnnotationAttributes overriddenAnnotationAttributes) {
    this.delegate = delegate;
    this.overriddenAnnotationName = overriddenAnnotationName;
    this.overriddenAnnotationAttributes = overriddenAnnotationAttributes;
  }

  @Override
  public Set<String> getAnnotationTypes() {
    Set<String> result = delegate.getAnnotationTypes();
    result.add( overriddenAnnotationName );
    return result;
  }

  @Override
  public Set<String> getMetaAnnotationTypes(String annotationName) {
    return delegate.getMetaAnnotationTypes(annotationName);
  }

  @Override
  public boolean hasAnnotation(String annotationName) {
    return overriddenAnnotationName.equals(annotationName) || delegate.hasAnnotation(annotationName);
  }

  @Override
  public boolean hasMetaAnnotation(String metaAnnotationName) {
    return delegate.hasMetaAnnotation(metaAnnotationName);
  }

  @Override
  public boolean hasAnnotatedMethods(String annotationName) {
    return delegate.hasAnnotatedMethods(annotationName);
  }

  @Override
  public Set<MethodMetadata> getAnnotatedMethods(String annotationName) {
    return delegate.getAnnotatedMethods(annotationName);
  }

  @Override
  public boolean isAnnotated(String annotationName) {
    return overriddenAnnotationName.equals(annotationName) || delegate.isAnnotated(annotationName);
  }

  @Override
  public Map<String, Object> getAnnotationAttributes(String annotationName) {
    if (overriddenAnnotationName.equals(annotationName)) {
      return overriddenAnnotationAttributes;
    }
    else {
      return delegate.getAnnotationAttributes(annotationName);
    }
  }

  @Override
  public Map<String, Object> getAnnotationAttributes(String annotationName, boolean classValuesAsString) {
    if (overriddenAnnotationName.equals(annotationName)) {
      if (classValuesAsString) {
        throw new UnsupportedOperationException();
      }
      return overriddenAnnotationAttributes;
    } else {
      return delegate.getAnnotationAttributes(annotationName);
    }
  }

  @Override
  public MultiValueMap<String, Object> getAllAnnotationAttributes(String annotationName) {
    if (overriddenAnnotationName.equals(annotationName)) {
      MultiValueMap<String, Object> result = new LinkedMultiValueMap<>();
      result.entrySet().stream().forEach(e -> result.add(e.getKey(), e.getValue()));
      return result;
    }
    else {
      return delegate.getAllAnnotationAttributes(annotationName);
    }
  }

  @Override
  public MultiValueMap<String, Object> getAllAnnotationAttributes(String annotationName, boolean classValuesAsString) {
    if (overriddenAnnotationName.equals(annotationName)) {
      if (classValuesAsString) {
        throw new UnsupportedOperationException();
      }
      MultiValueMap<String, Object> result = new LinkedMultiValueMap<>();
      result.entrySet().stream().forEach(e -> result.add(e.getKey(), e.getValue()));
      return result;
    }
    else {
      return delegate.getAllAnnotationAttributes(annotationName);
    }
  }

  @Override
  public String getClassName() {
    return delegate.getClassName();
  }

  @Override
  public boolean isInterface() {
    return delegate.isInterface();
  }

  @Override
  public boolean isAnnotation() {
    return delegate.isAnnotation();
  }

  @Override
  public boolean isAbstract() {
    return delegate.isAbstract();
  }

  @Override
  public boolean isConcrete() {
    return delegate.isConcrete();
  }

  @Override
  public boolean isFinal() {
    return delegate.isFinal();
  }

  @Override
  public boolean isIndependent() {
    return delegate.isIndependent();
  }

  @Override
  public boolean hasEnclosingClass() {
    return delegate.hasEnclosingClass();
  }

  @Override
  public String getEnclosingClassName() {
    return delegate.getEnclosingClassName();
  }

  @Override
  public boolean hasSuperClass() {
    return delegate.hasSuperClass();
  }

  @Override
  public String getSuperClassName() {
    return delegate.getSuperClassName();
  }

  @Override
  public String[] getInterfaceNames() {
    return delegate.getInterfaceNames();
  }

  @Override
  public String[] getMemberClassNames() {
    return delegate.getMemberClassNames();
  }
}
