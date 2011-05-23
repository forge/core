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

import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.jboss.forge.ForgeEnvironment;
import org.jboss.forge.shell.ShellPrintWriter;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.Topic;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Alias("list-properties")
@Topic("Shell Environment")
@Help("Lists all current forge properties")
public class ListPropertiesPlugin implements Plugin
{
   final ForgeEnvironment forge;
   private final ShellPrintWriter writer;

   @Inject
   public ListPropertiesPlugin(final ForgeEnvironment forge, ShellPrintWriter writer)
   {
      this.forge = forge;
      this.writer = writer;
   }

   @DefaultCommand
   public void listProperties()
   {
      Map<String, Object> properties = forge.getProperties();

      for (Entry<String, Object> entry : properties.entrySet())
      {
         String key = entry.getKey();
         Object value = entry.getValue();

         writer.print(key + "=");
         if (value != null)
         {
            writer.print(value.toString());
         }
         writer.println();
      }
   }
}
