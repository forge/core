/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.ui;

import java.io.PrintStream;

import org.jboss.aesh.console.AeshConsole;
import org.jboss.forge.addon.shell.ShellMessages;
import org.jboss.forge.addon.ui.output.UIOutput;

/**
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ShellUIOutputImpl implements UIOutput
{
   private final AeshConsole console;

   public ShellUIOutputImpl(AeshConsole console)
   {
      this.console = console;
   }

   @Override
   public PrintStream out()
   {
      return console.getShell().out();
   }

   @Override
   public PrintStream err()
   {
      return console.getShell().err();
   }

   @Override
   public void success(PrintStream out, String message)
   {
      ShellMessages.success(out, message);
   }

   @Override
   public void error(PrintStream out, String message)
   {
      ShellMessages.error(out, message);
   }

   @Override
   public void info(PrintStream out, String message)
   {
      ShellMessages.info(out, message);
   }

   @Override
   public void warn(PrintStream out, String message)
   {
      ShellMessages.warn(out, message);
   }
}
