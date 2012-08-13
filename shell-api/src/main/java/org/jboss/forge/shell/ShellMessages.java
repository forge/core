/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.shell;

/**
 * Used to generate properly formatted status messages.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public abstract class ShellMessages
{
   public static void success(final ShellPrintWriter writer, final String message)
   {
      writer.print(ShellColor.GREEN, "***SUCCESS*** ");
      writer.println(message);
   }

   public static void error(final ShellPrintWriter writer, final String message)
   {
      writer.print(ShellColor.RED, "***ERROR*** ");
      writer.println(message);
   }

   public static void info(final ShellPrintWriter writer, final String message)
   {
      writer.print(ShellColor.YELLOW, "***INFO*** ");
      writer.println(message);
   }

   public static void warn(final ShellPrintWriter writer, final String message)
   {
      writer.print(ShellColor.MAGENTA, "***WARNING*** ");
      writer.println(message);
   }
}
