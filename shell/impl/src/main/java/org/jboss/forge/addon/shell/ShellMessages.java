/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell;

import java.io.PrintStream;

import org.jboss.aesh.terminal.Color;
import org.jboss.aesh.terminal.TerminalColor;
import org.jboss.aesh.terminal.TerminalString;

/**
 * Used to generate properly formatted status messages.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public abstract class ShellMessages
{
   public static void success(final PrintStream writer, final String message)
   {
      writer.print(new TerminalString("***SUCCESS*** ", new TerminalColor(Color.GREEN, Color.DEFAULT)));
      writer.println(message);
   }

   public static void error(final PrintStream writer, final String message)
   {
      writer.print(new TerminalString("***ERROR*** ", new TerminalColor(Color.RED, Color.DEFAULT)));
      writer.println(message);
   }

   public static void info(final PrintStream writer, final String message)
   {
      writer.print(new TerminalString("***INFO*** ", new TerminalColor(Color.BLUE, Color.DEFAULT)));
      writer.println(message);
   }

   public static void warn(final PrintStream writer, final String message)
   {
      writer.print(new TerminalString("***WARNING*** ", new TerminalColor(Color.YELLOW, Color.DEFAULT)));
      writer.println(message);
   }
}
