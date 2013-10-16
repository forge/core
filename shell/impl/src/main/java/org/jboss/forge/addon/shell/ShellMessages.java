/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.shell;

import org.jboss.aesh.console.reader.AeshPrintStream;
import org.jboss.aesh.terminal.CharacterType;
import org.jboss.aesh.terminal.Color;
import org.jboss.aesh.terminal.TerminalString;

/**
 * Used to generate properly formatted status messages.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public abstract class ShellMessages
{
   public static void success(final AeshPrintStream writer, final String message)
   {
      writer.print(new TerminalString("***SUCCESS*** ", Color.DEFAULT_BG, Color.GREEN_TEXT, CharacterType.PLAIN));
      writer.println(message);
   }

   public static void error(final AeshPrintStream writer, final String message)
   {
      writer.print(new TerminalString("***ERROR*** ", Color.DEFAULT_BG, Color.RED_TEXT, CharacterType.PLAIN));
      writer.println(message);
   }

   public static void info(final AeshPrintStream writer, final String message)
   {
      writer.print(new TerminalString("***INFO*** ", Color.DEFAULT_BG, Color.BLUE_TEXT, CharacterType.PLAIN));
      writer.println(message);
   }

   public static void warn(final AeshPrintStream writer, final String message)
   {
      writer.print(new TerminalString("***WARNING*** ", Color.DEFAULT_BG, Color.YELLOW_TEXT, CharacterType.PLAIN));
      writer.println(message);
   }
}
