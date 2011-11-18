package org.jboss.forge.test;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Queue;

public class QueuedInputStream extends InputStream
{
   BufferedInputStream current;
   private final Queue<String> inputQueue;

   QueuedInputStream(final Queue<String> inputQueue)
   {
      super();
      this.inputQueue = inputQueue;
   }

   @Override
   public synchronized int available() throws IOException
   {
      requireCurrent();
      return current.available();
   }

   @Override
   public void close() throws IOException
   {
      requireCurrent();
      current.close();
   }

   @Override
   public synchronized void mark(final int readlimit)
   {
      requireCurrent();
      current.mark(readlimit);
   }

   @Override
   public boolean markSupported()
   {
      requireCurrent();
      return current.markSupported();
   }

   @Override
   public synchronized int read() throws IOException
   {
      requireCurrent();
      return current.read();
   }

   @Override
   public synchronized int read(final byte[] b, final int off, final int len) throws IOException
   {
      requireCurrent();
      return current.read(b, off, len);
   }

   @Override
   public synchronized void reset() throws IOException
   {
      current.reset();
   }

   @Override
   public synchronized long skip(final long n) throws IOException
   {
      requireCurrent();
      return current.skip(n);
   }

   /*
    * Utilities
    */
   synchronized private void requireCurrent()
   {
      try
      {
         if ((current == null) || (current.available() <= 0))
         {
            if (!inputQueue.isEmpty())
            {
               String line = inputQueue.remove();
               System.out.println(); // makes the output look like someone typed the input :)
               byte[] bytes = new byte[] {};

               if (line != null)
                  bytes = line.getBytes();

               current = new BufferedInputStream(new ByteArrayInputStream(bytes));
            }
            else
            {
              throw new IllegalStateException("End of stream: No more queued input.");
            }
         }
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }
}
