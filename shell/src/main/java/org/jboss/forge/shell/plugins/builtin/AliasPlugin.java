/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
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
         for (Entry<String, String> alias : registry.getAliases().entrySet()) {
            out.println(alias.getKey() + " = " + alias.getValue());
         }
      }
   }
}
