/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.shell.integration;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public enum NoopBufferManager implements BufferManager
{
   INSTANCE;

   @Override
   public void bufferOnlyMode()
   {

   }

   @Override
   public void directWriteMode()
   {

   }

   @Override
   public void flushBuffer()
   {

   }

   @Override
   public void write(int b)
   {

   }

   @Override
   public void write(byte b)
   {
   }

   @Override
   public void write(byte[] b)
   {

   }

   @Override
   public void write(byte[] b, int offset, int length)
   {

   }

   @Override
   public void write(String s)
   {
   }

   @Override
   public void directWrite(String s)
   {
   }

   @Override
   public void setBufferPosition(int row, int col)
   {
   }

   @Override
   public int getHeight()
   {
      return 0;
   }

   @Override
   public int getWidth()
   {
      return 0;
   }
}
