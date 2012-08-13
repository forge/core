/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.test.command;

import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.Plugin;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Alias("mnp")
public class MockNamedPlugin implements Plugin
{
   @Command(help = "A mock run command")
   public void run()
   {

   }

   @Command
   public void helpless()
   {

   }

   @DefaultCommand(help = "This is a mock default command")
   public void defaultCommand(@Option final String option)
   {

   }

   @Command
   public void normal(@Option(description = "THE OPTION") final String option)
   {

   }

   @Command("named")
   public void named(@Option(name = "named", defaultValue = "true") final String option)
   {

   }

   @Command
   public void multiOption(@Option(name = "named") final String option,
            @Option(name = "foo") final boolean foo)
   {

   }

   public void notCommand()
   {

   }
}
