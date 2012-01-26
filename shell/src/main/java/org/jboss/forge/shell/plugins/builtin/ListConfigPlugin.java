/*
 * JBoss, by Red Hat.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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

            for (ConfigurationScope scope : ConfigurationScope.values()) {
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
