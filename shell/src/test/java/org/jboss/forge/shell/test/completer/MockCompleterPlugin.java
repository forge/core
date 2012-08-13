/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.test.completer;

import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.Plugin;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class MockCompleterPlugin implements Plugin
{
   private boolean defaultInvoked = false;
   private boolean command1Invoked = false;
   private boolean command2Invoked = false;
   private boolean command3Invoked = false;
   private boolean command4Invoked = false;

   @DefaultCommand
   public void defaultCommand()
   {
      defaultInvoked = true;
   }

   @Command("command1")
   public void command1(@Option(description = "Option One", required = true) final int number,
            @Option(required = false) final String optional)
   {
      command1Invoked = true;
   }

   @Command("command2")
   public void command2(@Option(name = "option", description = "Option Two", required = true) final int number,
            @Option(required = false) final String optional)
   {
      command2Invoked = true;
   }

   @Command("command3")
   public void command3(
            @Option(name = "option", description = "Option One", required = true) final int number,
            @Option(name = "option2",
                     description = "Option Two",
                     required = true,
                     defaultValue = "default",
                     completer = MockValueCompleter.class) final int number2,
            @Option(required = false) final String optional)
   {
      command3Invoked = true;
   }

   @Command("command4")
   public void command4(
            @Option(name = "option", description = "Option One", required = true) final MockEnum number)
   {
      command4Invoked = true;
   }

   public boolean isCommand1Invoked()
   {
      return command1Invoked;
   }

   public boolean isCommand2Invoked()
   {
      return command2Invoked;
   }

   public boolean isCommand3Invoked()
   {
      return command3Invoked;
   }

   public boolean isCommand4Invoked()
   {
      return command4Invoked;
   }

   public boolean isDefaultInvoked()
   {
      return defaultInvoked;
   }
}
