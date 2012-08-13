/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.plugins.builtin;

import javax.inject.Inject;

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
@Help("Un-alias an aliased command. See also 'help alias'")
@Alias("unalias")
public class UnaliasPlugin implements Plugin
{
   @Inject
   private AliasRegistry registry;

   @DefaultCommand
   public void set(final PipeOut out,
            @Option(help = "The alias name to remove: E.g: 'mycommand'") final String[] aliases)
   {
      if ((aliases != null) && (aliases.length > 0))
      {
         for (String alias : aliases)
         {
            registry.removeAlias(alias);
         }
      }
   }
}
