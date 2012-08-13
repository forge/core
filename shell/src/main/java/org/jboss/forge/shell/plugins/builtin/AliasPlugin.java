/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.plugins.builtin;

import java.util.Arrays;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.jboss.forge.parser.java.util.Strings;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
@Help("Create an alias, aliases allow a string to be " +
         "substituted for a word when it is used as the " +
         "first word of a simple command. Ex.: 'alias \"ls=ls -a\"." +
         "See also: 'unalias'")
@Alias("alias")
public class AliasPlugin implements Plugin
{
   @Inject
   private AliasRegistry registry;

   @DefaultCommand
   public void set(final PipeOut out, @Option(help = "The alias definition: E.g: 'ls=ls -a'") final String[] tokens)
   {
      if ((tokens != null) && (tokens.length > 0))
      {
         String definition = Strings.join(Arrays.asList(tokens), " ");
         if (definition.contains("="))
         {
            String alias = definition.substring(0, definition.indexOf("="));
            String command = definition.substring(definition.indexOf("=") + 1);

            registry.createAlias(alias, command);
         }
      }
      else
      {
         for (Entry<String, String> alias : registry.getAliases().entrySet())
         {
            out.println(alias.getKey() + " = " + alias.getValue());
         }
      }
   }
}
