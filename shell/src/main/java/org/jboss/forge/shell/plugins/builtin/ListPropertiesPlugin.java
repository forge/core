/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
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
