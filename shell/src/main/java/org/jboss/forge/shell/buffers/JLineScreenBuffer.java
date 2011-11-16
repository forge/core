package org.jboss.forge.shell.buffers;

import jline.Terminal;
import jline.console.ConsoleReader;
import org.fusesource.jansi.Ansi;
import org.jboss.forge.shell.BufferManager;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author Mike Brock
 */
public class JLineScreenBuffer implements BufferManager
{
   private ConsoleReader reader;
   private Terminal terminal;
   private boolean directWrite = true;

   private int maxBufferSize = 1024 * 10;
   private ByteBuffer buffer;
   private int bufferSize = 0;


   public JLineScreenBuffer(ConsoleReader reader)
   {
      this.reader = reader;
      this.terminal = reader.getTerminal();
      this.buffer = ByteBuffer.allocateDirect(maxBufferSize);
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
   public synchronized void flushBuffer()
   {
      try
      {
         byte[] buf = new byte[2048];
         buffer.rewind();

         do
         {
            int i = 0;
            for (; i < buf.length && bufferSize > 0; i++)
            {
               bufferSize--;
               buf[i] = buffer.get();
            }
            reader.print(new String(buf, 0, i));
         }
         while (bufferSize > 0);

         bufferSize = 0;
         buffer.clear();
         reader.flush();
      }
      catch (IOException e)
      {
         throw new RuntimeException("could not flush", e);
      }
   }

   @Override
   public synchronized void write(byte b)
   {
      if (bufferSize + 1 >= maxBufferSize) flushBuffer();

      write(new byte[]{b});
      bufferSize++;
   }

   @Override
   public synchronized void write(byte[] b)
   {
      if (bufferSize + b.length >= maxBufferSize) flushBuffer();

      write(b, 0, b.length);
      bufferSize += b.length;
   }

   @Override
   public synchronized void write(byte[] b, int offset, int length)
   {
      if (bufferSize + length >= maxBufferSize) flushBuffer();

      write(new String(b, offset, length));
      bufferSize += length;
   }

   @Override
   public synchronized void write(String s)
   {
      try
      {
         reader.print(s);
         if (directWrite)
         {
            reader.flush();
         }
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
