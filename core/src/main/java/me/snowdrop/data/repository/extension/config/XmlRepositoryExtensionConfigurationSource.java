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
package me.snowdrop.data.repository.extension.config;

import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.core.env.Environment;
import org.springframework.data.repository.config.XmlRepositoryConfigurationSource;
import org.w3c.dom.Element;

public class XmlRepositoryExtensionConfigurationSource extends XmlRepositoryConfigurationSource
        implements RepositoryExtensionConfigurationSource {

  private static final String EXTENDED_REPOSITORY_IMPLEMENTATION_POSTFIX = "extended-repository-implementation-postfix";

  public XmlRepositoryExtensionConfigurationSource(Element element, ParserContext context, Environment environment) {
    super(element, context, environment);
  }

  @Override
  public String getExtendedRepositoryImplementationPostfix() {
    return getAttribute(EXTENDED_REPOSITORY_IMPLEMENTATION_POSTFIX);
  }
}
