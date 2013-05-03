/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.aesh;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import javax.inject.Singleton;

import org.jboss.forge.aesh.spi.ShellStreamProvider;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Singleton
public class TestShellStreamProvider implements ShellStreamProvider
{
   private PipedOutputStream stdin;
   private ByteArrayOutputStream stdout;
   private ByteArrayOutputStream stderr;
   private PipedInputStream inputStream;

   public TestShellStreamProvider()
   {
      stdin = new PipedOutputStream();
      stdout = new ByteArrayOutputStream();
      stderr = new ByteArrayOutputStream();
      inputStream = null;
   }

   @Override
   public InputStream getInputStream()
   {
      if (inputStream == null)
      {
         try
         {
            this.inputStream = new PipedInputStream(stdin);
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }
      }
      return inputStream;
   }

   public OutputStream getStdIn()
   {
      return stdin;
   }

   @Override
   public ByteArrayOutputStream getStdOut()
   {
      return stdout;
   }

   @Override
   public ByteArrayOutputStream getStdErr()
   {
      return stderr;
   }

   @Override
   public void reset()
   {
      stdin = new PipedOutputStream();
      stdout = new ByteArrayOutputStream();
      stderr = new ByteArrayOutputStream();
   }

}
