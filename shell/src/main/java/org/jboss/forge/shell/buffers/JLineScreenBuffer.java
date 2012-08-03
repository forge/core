package org.jboss.forge.shell.buffers;

import org.jboss.forge.shell.integration.BufferManager;
import org.jboss.forge.shell.console.jline.Terminal;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * A buffer to wrap JLine.
 *
 * @author Mike Brock
 */
public class JLineScreenBuffer implements BufferManager
{
   //
   private OutputStream outputStream;
   private Terminal terminal;
   private boolean directWrite = true;

   private int maxBufferSize = 1024 * 10;
   private ByteBuffer buffer;
   private int bufferSize = 0;

   public JLineScreenBuffer(Terminal terminal, OutputStream outputStream)
   {
      this.terminal = terminal;
      this.outputStream = outputStream;
      this.buffer = ByteBuffer.allocateDirect(maxBufferSize);
   }

   @Override
   public void bufferOnlyMode()
   {
      directWrite = false;
   }

   @Override
   public synchronized void directWriteMode()
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
               buf[i] = buffer.get();
               bufferSize--;
            }

            outputStream.write(buf, 0, i);
         }
         while (bufferSize > 0);

         bufferSize = 0;
         buffer.clear();
         outputStream.flush();
      }
      catch (IOException e)
      {
         throw new RuntimeException("could not flush", e);
      }
   }

   @Override
   public synchronized void write(int b)
   {
      if (bufferSize + 1 >= maxBufferSize)
      {
         flushBuffer();
      }

      buffer.put((byte) b);
      bufferSize++;
      _flush();
   }

   @Override
   public synchronized void write(byte b)
   {
      if (bufferSize + 1 >= maxBufferSize)
      {
         flushBuffer();
      }

      buffer.put(b);
      bufferSize++;
      _flush();
   }

   @Override
   public synchronized void write(byte[] b)
   {
      if (bufferSize + b.length >= maxBufferSize)
      {
         flushBuffer();
         write(b);
      }

      buffer.put(b, 0, b.length);
      bufferSize += b.length;
      _flush();
   }

   @Override
   public synchronized void write(byte[] b, int offset, int length)
   {
      if (bufferSize + length >= maxBufferSize)
      {
         flushBuffer();
         write(b, offset, length);
      }

      buffer.put(b, offset, length);
      bufferSize += length;
      _flush();
   }

   @Override
   public synchronized void write(String s)
   {
      if (bufferSize + s.length() >= maxBufferSize)
      {
         flushBuffer();
         write(s);
      }

      buffer.put(s.getBytes());
      bufferSize += s.length();
      _flush();
   }

   /**
    * For data that exceeds the maximum size of the buffer, write out the data in segments.
    *
    * @param b
    * @param offset
    * @param length
    */
   @SuppressWarnings("unused")
   private void segmentedWrite(byte[] b, int offset, int length)
   {
      if (b.length > maxBufferSize)
      {

         int segs = b.length / maxBufferSize;
         int tail = b.length % maxBufferSize;
         for (int i = 0; i < segs; i++)
         {
            write(b, (i + offset) * maxBufferSize, maxBufferSize);
         }
         write(b, (segs + 1) * maxBufferSize, tail);
      }
      else
      {
         write(b, offset, length);
      }
   }

   private void _flush()
   {
      if (directWrite)
         flushBuffer();
   }

   @Override
   public void directWrite(String s)
   {
      try
      {
         outputStream.write(s.getBytes());
         outputStream.flush();
      }
      catch (IOException e)
      {
         throw new RuntimeException("could not write", e);
      }
   }

   public void setBufferPosition(int row, int col)
   {
      // try
      // {
      // reader.print(new Ansi().cursor(row, col).toString());
      // }
      // catch (IOException e)
      // {
      // throw new RuntimeException("could not set buffer position", e);
      // }
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
