/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.command;

import java.io.IOException;

import org.jboss.forge.addon.ui.annotation.Command;
import org.jboss.forge.addon.ui.annotation.Option;
import org.jboss.forge.addon.ui.annotation.predicate.NonGUIEnabledPredicate;
import org.jboss.forge.addon.ui.output.UIOutput;
import org.jboss.forge.furnace.util.Strings;

/**
 * 
 * @author <a href="mailto:md.benhassine@gmail.com">Mahmoud Ben Hassine</a>
 */
public class SystemPropertiesCommand
{

   @Command(value = "system-property-get", help = "Get one or all system properties", enabled = NonGUIEnabledPredicate.class)
   public void getSystemProperty(
            @Option(value = "named", description = "The property name") final String propertyName,
            final UIOutput output) throws IOException
   {
      if (!Strings.isNullOrEmpty(propertyName))
      {
         output.out().println(System.getProperty(propertyName));
      }
      else
      {
         System.getProperties().store(output.out(), null);
      }
   }

   @Command(value = "system-property-set", help = "Set a system property", enabled = NonGUIEnabledPredicate.class)
   public String setSystemProperty(
            @Option(value = "named", description = "The property name", required = true) final String propertyName,
            @Option(value = "value", description = "The property value", required = true) final String propertyValue)
   {
      return System.setProperty(propertyName, propertyValue);
   }

}