/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.resource.transaction.file;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * This class provides an initial value for the {@link InputStream#available()} method in the delegated stream (Eg: the
 * file size).
 * 
 * After {@link InputStream#read()} is called, it returns the available value from the delegated {@link InputStream}
 * 
 * This is used in {@link FileResourceTransactionImpl} because some callers use the {@link InputStream#available()}
 * method to check if the stream can be read (ShrinkWrap Resolver - however they shouldn't as per the javadoc).
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class AvailableInputStreamWrapper extends FilterInputStream
{
   private final int initialAvailable;
   private volatile boolean streamRead;

   protected AvailableInputStreamWrapper(InputStream in, int available)
   {
      super(in);
      this.initialAvailable = available;
   }

   @Override
   public int available() throws IOException
   {
      if (streamRead)
      {
         return super.available();
      }
      else
      {
         return initialAvailable;
      }
   }

   @Override
   public int read() throws IOException
   {
      streamRead = true;
      return super.read();
   }

   @Override
   public int read(byte[] b) throws IOException
   {
      streamRead = true;
      return super.read(b);
   }

   @Override
   public int read(byte[] b, int off, int len) throws IOException
   {
      streamRead = true;
      return super.read(b, off, len);
   }

   @Override
   public synchronized void reset() throws IOException
   {
      streamRead = true;
      super.reset();
   }

   @Override
   public long skip(long n) throws IOException
   {
      streamRead = true;
      return super.skip(n);
   }

   @Override
   public synchronized void mark(int readlimit)
   {
      streamRead = true;
      super.mark(readlimit);
   }
}
