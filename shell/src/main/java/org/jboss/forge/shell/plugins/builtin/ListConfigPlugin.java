/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.plugins.builtin;

import java.util.Iterator;

import javax.inject.Inject;

import org.jboss.forge.env.Configuration;
import org.jboss.forge.env.ConfigurationScope;
import org.jboss.forge.shell.ShellColor;
import org.jboss.forge.shell.ShellPrintWriter;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.Topic;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Alias("list-config")
@Topic("Shell Environment")
@Help("Lists all Forge configuration for the current user and project (if working on a project.)")
public class ListConfigPlugin implements Plugin
{
   private final ShellPrintWriter writer;
   private final Configuration config;

   @Inject
   public ListConfigPlugin(final Configuration forge, final ShellPrintWriter writer)
   {
      this.config = forge;
      this.writer = writer;
   }

   @DefaultCommand
   public void listProperties()
   {
      Iterator<?> properties = config.getKeys();

      while (properties.hasNext())
      {
         Object key = properties.next();

         if (key != null)
         {
            writer.print(ShellColor.BOLD, key.toString());
            writer.print("=");

            for (ConfigurationScope scope : ConfigurationScope.values())
            {
               Configuration scoped = config.getScopedConfiguration(scope);
               if (scoped != null)
               {
                  Object value = scoped.getProperty(key.toString());
                  writer.print(ShellColor.YELLOW, scope.name() + ": ");
                  if (value != null)
                  {
                     writer.print("[" + value.toString() + "] ");
                  }
                  else
                     writer.print("[] ");
               }
            }
            writer.println();
         }
      }
   }

}
