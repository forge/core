/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.shell;

/**
 * @author Mike Brock .
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface ShellPrintWriter
{
   /**
    * Write the given bytes to the console.
    */
   public void write(int b);

   public void write(byte b);

   public void write(byte[] b);

   public void write(byte[] b, int offset, int length);

   /**
    * Print output to the console.
    */
   void print(String output);

   /**
    * Print output to the console, followed by the newline character.
    */
   void println(String output);

   /**
    * Print a blank line to the console.
    */
   void println();

   /**
    * Print color output to the console.
    */
   void print(ShellColor color, String output);

   /**
    * Print color output to the console, followed by the newline character.
    */
   void println(ShellColor color, String output);

   /**
    * Render a color for the current terminal emulation by encapsulating the string is the appropriate escape codes
    */
   public String renderColor(ShellColor color, String output);

   /**
    * Flush output.
    */
   public void flush();
}
