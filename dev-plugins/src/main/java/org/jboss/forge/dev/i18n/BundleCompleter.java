/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
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
