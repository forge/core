/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.util;

import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.ShellColor;
import org.jboss.forge.shell.plugins.PipeOut;

/**
 * @author Mike Brock .
 */
public class PipeOutImpl implements PipeOut
{
   private final StringBuilder buffer = new StringBuilder();
   private final Shell shell;
   private boolean piped = false;

   public PipeOutImpl(final Shell shell)
   {
      this.shell = shell;
   }

   @Override
   public void write(int b)
   {
      if (piped)
      {
         buffer.append(b);
      }
      else
      {
         shell.write(b);
      }
   }

   @Override
   public void write(final byte b)
   {
      if (piped)
      {
         buffer.append((char) b);
      }
      else
      {
         shell.print(String.valueOf((char) b));
      }
   }

   @Override
   public void write(byte[] b)
   {
      if (piped)
      {
         buffer.append(new String(b));
      }
      else
      {
         shell.write(b);
      }
   }

   @Override
   public void write(byte[] b, int offset, int length)
   {
      if (piped)
      {
         buffer.append(new String(b, offset, length));
      }
      else
      {
         shell.write(b, offset, length);
      }
   }

   @Override
   public void print(final String s)
   {
      if (piped)
      {
         buffer.append(s);
      }
      else
      {
         shell.print(s);
      }
   }

   @Override
   public void println(final String s)
   {
      if (piped)
      {
         buffer.append(s).append("\n");
      }
      else
      {
         shell.println(s);
      }
   }

   @Override
   public void println()
   {
      if (piped)
      {
         buffer.append("\n");
      }
      else
      {
         shell.println();
      }
   }

   @Override
   public void print(final ShellColor color, final String s)
   {
      print(renderColor(color, s));
   }

   @Override
   public void println(final ShellColor color, final String s)
   {
      println(renderColor(color, s));
   }

   @Override
   public String renderColor(final ShellColor color, final String s)
   {
      return piped ? s : shell.renderColor(color, s);
   }

   @Override
   public boolean isPiped()
   {
      return piped;
   }

   @Override
   public void setPiped(final boolean v)
   {
      this.piped = v;
   }

   @Override
   public String getBuffer()
   {
      return buffer.toString();
   }

   @Override
   public void flush()
   {
      shell.flush();
   }
}
