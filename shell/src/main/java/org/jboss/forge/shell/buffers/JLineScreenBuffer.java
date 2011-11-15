package org.jboss.forge.shell.buffers;

import jline.Terminal;
import jline.console.ConsoleReader;
import org.fusesource.jansi.Ansi;
import org.jboss.forge.shell.BufferManager;

import java.io.IOException;

/**
 * @author Mike Brock
 */
public class JLineScreenBuffer implements BufferManager
{
   private ConsoleReader reader;
   private Terminal terminal;
   private boolean directWrite = true;

   public JLineScreenBuffer(ConsoleReader reader)
   {
      this.reader = reader;
      this.terminal = reader.getTerminal();
   }

   @Override
   public void bufferOnlyMode()
   {
      directWrite = false;
   }

   @Override
   public void directWriteMode()
   {
      directWrite = true;
      flushBuffer();
   }

   @Override
   public void flushBuffer()
   {
      try
      {
         reader.flush();
      }
      catch (IOException e)
      {
         throw new RuntimeException("could not flush", e);
      }
   }

   @Override
   public void write(byte b)
   {
      write(new byte[]{b});
   }

   @Override
   public void write(byte[] b)
   {
      write(b, 0, b.length);
   }

   @Override
   public void write(byte[] b, int offset, int length)
   {
      write(new String(b, offset, length));
   }

   @Override
   public void write(String s)
   {
      try
      {
         reader.print(s);
         if (directWrite) reader.flush();
      }
      catch (IOException e)
      {
         throw new RuntimeException("could not write", e);
      }
   }

   public void setBufferPosition(int row, int col)
   {
      try
      {
         reader.print(new Ansi().cursor(row, col).toString());
      }
      catch (IOException e)
      {
         throw new RuntimeException("could not set buffer position", e);
      }
   }

   @Override
   public int getHeight()
   {
      return terminal.getHeight();
   }

   @Override
   public int getWidth()
   {
      return terminal.getWidth();
   }
}
