/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.eclipse.org/legal/epl-v10.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.forge.dev.i18n;

import javax.inject.Inject;

import org.jboss.forge.resources.PropertiesFileResource;
import org.jboss.forge.shell.completer.SimpleTokenCompleter;
import org.jboss.forge.shell.plugins.Current;

/**
 * @author <a href="mailto:gegastaldi@gmail.com">George Gastaldi</a>
 *
 */
public class BundleCompleter extends SimpleTokenCompleter
{
   @Inject
   @Current
   PropertiesFileResource propertiesFileResource;

   @Override
   public Iterable<?> getCompletionTokens()
   {
      return propertiesFileResource.getKeys();
   }
}
