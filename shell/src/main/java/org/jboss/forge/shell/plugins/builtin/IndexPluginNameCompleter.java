/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.plugins.builtin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.jboss.forge.env.Configuration;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.completer.SimpleTokenCompleter;
import org.jboss.forge.shell.util.PluginRef;
import org.jboss.forge.shell.util.PluginUtil;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class IndexPluginNameCompleter extends SimpleTokenCompleter
{
   @Inject
   private Shell shell;

   @Inject
   private Configuration config;

   @Override
   public Iterable<?> getCompletionTokens()
   {
      List<String> plugins = new ArrayList<String>();
      try
      {
         List<PluginRef> refs = PluginUtil.findPluginSilent(shell, config, "*");
         for (PluginRef pluginRef : refs)
         {
            plugins.add(pluginRef.getName());
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }

      Collections.sort(plugins);

      return plugins;
   }

}
