/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.plugins.builtin;

import org.jboss.forge.shell.InstalledPluginRegistry;
import org.jboss.forge.shell.completer.SimpleTokenCompleter;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class InstalledPluginCompleter extends SimpleTokenCompleter
{

   @Override
   public Iterable<?> getCompletionTokens()
   {
      return InstalledPluginRegistry.list();
   }

}
