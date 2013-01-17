/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.aesh.commands;

import javax.inject.Inject;

import org.jboss.aesh.console.Console;
import org.jboss.forge.aesh.ShellContext;
import org.jboss.forge.ui.Result;
import org.jboss.forge.ui.UICommand;
import org.jboss.forge.ui.UICommandID;
import org.jboss.forge.ui.UIContext;
import org.jboss.forge.ui.UIInput;
import org.jboss.forge.ui.UIValidationContext;
import org.jboss.forge.ui.base.SimpleUICommandID;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public class ExitCommand implements UICommand
{
   private Console console;

   @Inject
   private UIInput<String> exit;

   @Inject
   private UIInput<String> quit;

   public ExitCommand(Console console)
   {
      setConsole(console);
   }

   @Override
   public UICommandID getId()
   {
      return new SimpleUICommandID("exit", "Exit the shell");
   }

   private void setConsole(Console console)
   {
      this.console = console;
   }

   @Override
   public void initializeUI(UIContext context) throws Exception
   {
      exit.setLabel("exit");
      exit.setRequired(true);
      context.getUIBuilder().add(exit);

      quit.setLabel("quit");
      quit.setRequired(true);
      context.getUIBuilder().add(quit);

      if (context instanceof ShellContext)
      {
         ((ShellContext) context).setStandalone(false);
      }
   }

   @Override
   public void validate(UIValidationContext context)
   {
   }

   @Override
   public Result execute(UIContext context) throws Exception
   {
      console.stop();
      return Result.success("");
   }

}
