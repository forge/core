/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.buffers;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.fusesource.jansi.internal.WindowsSupport;
import org.jboss.forge.shell.util.OSUtils;

/**
 * @author Mike Brock
 */
public class ConsoleInputSession
{
   private InputStream consoleStream;
   private InputStream externalInputStream;

   /*
    * A queue to hold characters that have been read from the raw console. This is declared as static, as a hack, to
    * allow characters to be read by an eventually terminating reader thread without being lost. This is necessary due
    * to the blocking read on System.in in the reader thread. One reader thread can be blocked reading System.in while
    * awaiting termination during an internal restart of Forge, and another reader thread would have just started but
    * would be awaiting the lock on System.in.
    */
   private static final ArrayBlockingQueue<Integer> blockingQueue = new ArrayBlockingQueue<Integer>(1000);
   
   private boolean isEmbedded;

   private volatile boolean connected;

   public ConsoleInputSession(InputStream consoleStream, boolean isEmbedded)
   {
      this.consoleStream = consoleStream;
      this.connected = true;
      this.isEmbedded = isEmbedded;

      this.externalInputStream = new InputStream()
      {
         private Integer b;

         @Override
         public int read() throws IOException
         {
            try
            {
               b = blockingQueue.poll(365, TimeUnit.DAYS);
               if (b != null)
               {
                  return b;
               }
            }
            catch (InterruptedException e)
            {
               //
            }
            return -1;
         }
      };

      startReader();
   }

   private void startReader()
   {
      Thread readerThread = null;
      if (OSUtils.isWindows() && !isEmbedded)
      {
         readerThread = new WindowsReaderThread();
      }
      else
      {
         readerThread = new GenericReaderThread();
      }
      readerThread.setDaemon(true);
      readerThread.start();
   }

   public void interruptPipe()
   {
      for(byte b : OSUtils.getLineSeparator().getBytes())
      {
         blockingQueue.offer((int) b);
      }
   }

   public void stop()
   {
      connected = false;
   }

   public InputStream getExternalInputStream()
   {
      return externalInputStream;
   }

   /**
    * The generic reader thread implementation for OSes other than Windows. Relies on reading from System,in with the
    * terminal having being set to raw mode through stty.
    */
   private final class GenericReaderThread extends Thread
   {
      @Override
      public void run()
      {
         while (connected)
         {
            try
            {
               int read = consoleStream.read();
               blockingQueue.put(read);
               Thread.sleep(10);
            }
            catch (IOException e)
            {
               if (connected)
               {
                  connected = false;
                  throw new RuntimeException("broken pipe");
               }
            }
            catch (InterruptedException e)
            {
               // Stop reading
               break;
            }
         }
      }
   }

   /**
    * The reader thread for Windows. Delegates to Jansi to obtain the keystrokes since System.in does not provide
    * keystrokes for arrow keys in a clean way.
    * 
    * See FORGE-942: the keystrokes for special events are available via System.in only when some special sequence of
    * events is triggered, and not in all cases.
    */
   private final class WindowsReaderThread extends Thread
   {
      @Override
      public void run()
      {
         while (connected)
         {
            try
            {
               int read = WindowsSupport.readByte();
               blockingQueue.put(read);
               Thread.sleep(10);
            }
            catch (InterruptedException e)
            {
               // Stop reading
               break;
            }
         }
      }
   }

}
