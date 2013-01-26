/*
 * Copyright (c) 2002-2007, Marc Prud'hommeaux. All rights reserved.
 *
 * This software is distributable under the BSD license. See the terms of the
 * BSD license in the documentation provided with this software.
 */

package org.jboss.forge.shell.console.jline;

import static org.jboss.forge.shell.console.jline.UnixTerminal.UnixKey.ARROW_DOWN;
import static org.jboss.forge.shell.console.jline.UnixTerminal.UnixKey.ARROW_LEFT;
import static org.jboss.forge.shell.console.jline.UnixTerminal.UnixKey.ARROW_PREFIX;
import static org.jboss.forge.shell.console.jline.UnixTerminal.UnixKey.ARROW_RIGHT;
import static org.jboss.forge.shell.console.jline.UnixTerminal.UnixKey.ARROW_START;
import static org.jboss.forge.shell.console.jline.UnixTerminal.UnixKey.ARROW_UP;
import static org.jboss.forge.shell.console.jline.UnixTerminal.UnixKey.DEL_THIRD;
import static org.jboss.forge.shell.console.jline.UnixTerminal.UnixKey.END_CODE;
import static org.jboss.forge.shell.console.jline.UnixTerminal.UnixKey.HOME_CODE;
import static org.jboss.forge.shell.console.jline.UnixTerminal.UnixKey.O_PREFIX;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.jboss.forge.shell.console.jline.console.Key;

/**
 * Terminal that is used for unix platforms. Terminal initialization is handled by issuing the <em>stty</em> command
 * against the <em>/dev/tty</em> file to disable character echoing and enable character input. All known unix systems
 * (including Linux and Macintosh OS X) support the <em>stty</em>), so this implementation should work for an reasonable
 * POSIX system.
 * 
 * @author <a href="mailto:mwp1@cornell.edu">Marc Prud'hommeaux</a>
 * @author <a href="mailto:dwkemp@gmail.com">Dale Kemp</a>
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @author <a href="mailto:jbonofre@apache.org">Jean-Baptiste Onofré</a>
 * @since 2.0
 */
public class UnixTerminal
         extends TerminalSupport
{
   private final org.jboss.forge.shell.console.jline.internal.TerminalLineSettings settings = new org.jboss.forge.shell.console.jline.internal.TerminalLineSettings();

   private final org.jboss.forge.shell.console.jline.internal.ReplayPrefixOneCharInputStream replayStream;

   private final InputStreamReader replayReader;

   public UnixTerminal() throws Exception
   {
      super(true);

      this.replayStream = new org.jboss.forge.shell.console.jline.internal.ReplayPrefixOneCharInputStream(
               org.jboss.forge.shell.console.jline.internal.Configuration.getInputEncoding());
      this.replayReader = new InputStreamReader(replayStream, replayStream.getEncoding());
   }

   protected org.jboss.forge.shell.console.jline.internal.TerminalLineSettings getSettings()
   {
      return settings;
   }

   /**
    * Remove line-buffered input by invoking "stty -icanon min 1" against the current terminal.
    */
   @Override
   public void init() throws Exception
   {
      super.init();

      setAnsiSupported(true);

      // set the console to be character-buffered instead of line-buffered
      settings.set("-icanon min 1");

      setEchoEnabled(false);
   }

   /**
    * Restore the original terminal configuration, which can be used when shutting down the console reader. The
    * ConsoleReader cannot be used after calling this method.
    */
   @Override
   public void restore() throws Exception
   {
      settings.restore();
      super.restore();
      // print a newline after the terminal exits.
      // this should probably be a configurable.
      System.out.println();
   }

   /**
    * Returns the value of <tt>stty columns</tt> param.
    */
   @Override
   public int getWidth()
   {
      int w = settings.getProperty("columns");
      return w < 1 ? DEFAULT_WIDTH : w;
   }

   /**
    * Returns the value of <tt>stty rows>/tt> param.
    */
   @Override
   public int getHeight()
   {
      int h = settings.getProperty("rows");
      return h < 1 ? DEFAULT_HEIGHT : h;
   }

   @Override
   public synchronized void setEchoEnabled(final boolean enabled)
   {
      try
      {
         if (enabled)
         {
            settings.set("echo");
         }
         else
         {
            settings.set("-echo");
         }
         super.setEchoEnabled(enabled);
      }
      catch (Exception e)
      {
         org.jboss.forge.shell.console.jline.internal.Log.error("Failed to ", (enabled ? "enable" : "disable"),
                  " echo: ", e);
      }
   }

   @Override
   public int readVirtualKey(final InputStream in) throws IOException
   {
      int c = readCharacter(in);

      if (Key.valueOf(c) == Key.DELETE && settings.getProperty("erase") == Key.DELETE.code)
      {
         c = org.jboss.forge.shell.console.jline.console.Key.BACKSPACE.code;
      }

      UnixKey key = UnixKey.valueOf(c);

      // in Unix terminals, arrow keys are represented by a sequence of 3 characters. E.g., the up arrow key yields 27,
      // 91, 68
      if (key == ARROW_START)
      {
         // also the escape key is 27 thats why we read until we have something different than 27
         // this is a bugfix, because otherwise pressing escape and than an arrow key was an undefined state
         while (key == ARROW_START)
         {
            c = readCharacter(in);
            key = UnixKey.valueOf(c);
         }

         if (key == ARROW_PREFIX || key == O_PREFIX)
         {
            c = readCharacter(in);
            key = UnixKey.valueOf(c);

            if (key == ARROW_UP)
            {
               return org.jboss.forge.shell.console.jline.console.Key.CTRL_P.code;
            }
            else if (key == ARROW_DOWN)
            {
               return org.jboss.forge.shell.console.jline.console.Key.CTRL_N.code;
            }
            else if (key == ARROW_LEFT)
            {
               return org.jboss.forge.shell.console.jline.console.Key.CTRL_B.code;
            }
            else if (key == ARROW_RIGHT)
            {
               return org.jboss.forge.shell.console.jline.console.Key.CTRL_F.code;
            }
            else if (key == HOME_CODE)
            {
               return org.jboss.forge.shell.console.jline.console.Key.CTRL_A.code;
            }
            else if (key == END_CODE)
            {
               return org.jboss.forge.shell.console.jline.console.Key.CTRL_E.code;
            }
            else if (key == DEL_THIRD)
            {
               readCharacter(in); // read 4th & ignore
               return org.jboss.forge.shell.console.jline.console.Key.DELETE.code;
            }
         }
      }

      // handle unicode characters, thanks for a patch from amyi@inf.ed.ac.uk
      if (c > 128)
      {
         // handle unicode characters longer than 2 bytes,
         // thanks to Marc.Herbert@continuent.com
         replayStream.setInput(c, in);
         // replayReader = new InputStreamReader(replayStream, encoding);
         c = replayReader.read();
      }

      return c;
   }

   /**
    * Unix keys.
    */
   public static enum UnixKey
   {
      ARROW_START(27),

      ARROW_PREFIX(91),

      ARROW_LEFT(68),

      ARROW_RIGHT(67),

      ARROW_UP(65),

      ARROW_DOWN(66),

      O_PREFIX(79),

      HOME_CODE(72),

      END_CODE(70),

      DEL_THIRD(51),

      DEL_SECOND(126), ;

      public final short code;

      UnixKey(final int code)
      {
         this.code = (short) code;
      }

      private static final Map<Short, UnixKey> codes;

      static
      {
         Map<Short, UnixKey> map = new HashMap<Short, UnixKey>();

         for (UnixKey key : UnixKey.values())
         {
            map.put(key.code, key);
         }

         codes = map;
      }

      public static UnixKey valueOf(final int code)
      {
         return codes.get((short) code);
      }
   }
}