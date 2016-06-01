/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.aesh;

import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.aesh.console.Config;
import org.jboss.aesh.console.reader.AeshStandardStream;
import org.jboss.aesh.console.reader.ConsoleInputSession;
import org.jboss.aesh.console.settings.Settings;
import org.jboss.aesh.terminal.CursorPosition;
import org.jboss.aesh.terminal.Shell;
import org.jboss.aesh.terminal.Terminal;
import org.jboss.aesh.terminal.TerminalSize;
import org.jboss.aesh.util.ANSI;
import org.jboss.forge.addon.shell.ShellHandleImpl;

/**
 * Used in {@link ShellHandleImpl}
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ForgeTerminal implements Terminal, Shell
{
   private final org.jboss.forge.addon.shell.spi.Terminal delegate;

   private ConsoleInputSession inputSession;
   private PrintStream stdOut;
   private PrintStream stdErr;
   private Settings settings;
   private boolean mainBuffer = true;
   private boolean echoEnabled;
   private TerminalSize terminalSize;
   private static final Logger logger = Logger.getLogger(ForgeTerminal.class.getName());

   public ForgeTerminal(org.jboss.forge.addon.shell.spi.Terminal delegate)
   {
      this.delegate = delegate;
   }

   @Override
   public void init(Settings settings)
   {
      this.settings = settings;

      // setting up input
      // input = new
      // ConsoleInputSession(settings.getInputStream()).getExternalInputStream();
      inputSession = new ConsoleInputSession(settings.getInputStream());

      this.stdOut = settings.getStdOut();
      this.stdErr = settings.getStdErr();
      delegate.initialize();
   }

   /**
    * Return the row position if we use a ansi terminal Send a terminal: '<ESC>[6n' and we receive the position as: '
    * <ESC>[n;mR' where n = current row and m = current column
    */
   @Override
   public CursorPosition getCursor()
   {
      if (settings.isAnsiConsole() && Config.isOSPOSIXCompatible())
      {
         try
         {
            StringBuilder col = new StringBuilder(4);
            StringBuilder row = new StringBuilder(4);
            out().print(ANSI.CURSOR_ROW);
            out().flush();
            boolean gotSep = false;
            // read the position
            int[] input = read();

            for (int i = 2; i < input.length - 1; i++)
            {
               if (input[i] == 59) // we got a ';' which is the separator
                  gotSep = true;
               else
               {
                  if (gotSep)
                     col.append((char) input[i]);
                  else
                     row.append((char) input[i]);
               }
            }
            return new CursorPosition(Integer.parseInt(row.toString()),
                     Integer.parseInt(col.toString()));
         }
         catch (Exception e)
         {
            if (settings.isLogging())
               logger.log(Level.SEVERE,
                        "Failed to find current row with ansi code: ", e);
            return new CursorPosition(-1, -1);
         }
      }
      return new CursorPosition(-1, -1);
   }

   @Override
   public void setCursor(CursorPosition position)
   {
      if (getSize().isPositionWithinSize(position))
      {
         out().print(position.asAnsi());
         out().flush();
      }
   }

   @Override
   public void moveCursor(int rows, int columns)
   {
      CursorPosition cp = getCursor();
      cp.move(rows, columns);
      if (getSize().isPositionWithinSize(cp))
      {
         setCursor(cp);
      }
   }

   @Override
   public void clear()
   {
      out().print(ANSI.CLEAR_SCREEN);
      out().flush();
   }

   @Override
   public boolean isMainBuffer()
   {
      return mainBuffer;
   }

   @Override
   public void enableAlternateBuffer()
   {
      if (isMainBuffer())
      {
         out().print(ANSI.ALTERNATE_BUFFER);
         out().flush();
         mainBuffer = false;
      }
   }

   @Override
   public void enableMainBuffer()
   {
      if (!isMainBuffer())
      {
         out().print(ANSI.MAIN_BUFFER);
         out().flush();
         mainBuffer = true;
      }
   }

   /**
    * @see org.jboss.aesh.terminal.Terminal
    */
   @Override
   public int[] read() throws IOException
   {
      return inputSession.readAll();
   }

   /**
    * @see org.jboss.aesh.terminal.Terminal
    */
   @Override
   public boolean isEchoEnabled()
   {
      return echoEnabled;
   }

   @Override
   public Shell getShell()
   {
      return this;
   }

   @Override
   public AeshStandardStream in()
   {
      return null;
   }

   @Override
   public PrintStream err()
   {
      return stdErr;
   }

   @Override
   public PrintStream out()
   {
      return stdOut;
   }

   @Override
   public void reset() throws IOException
   {
   }

   @Override
   public TerminalSize getSize()
   {
      int height = delegate.getHeight();
      int width = delegate.getWidth();
      if (terminalSize == null || (terminalSize.getHeight() != height || terminalSize.getWidth() != width))
      {
         terminalSize = new TerminalSize(height, width);
      }
      return terminalSize;
   }

   @Override
   public void close() throws IOException
   {
      inputSession.stop();
      delegate.close();
   }

   @Override
   public void writeToInputStream(String data)
   {
      inputSession.writeToInput(data);
   }

   @Override
   public void changeOutputStream(PrintStream output)
   {
      stdOut = output;
   }

   @Override
   public boolean hasInput()
   {
      return inputSession.hasInput();
   }

}