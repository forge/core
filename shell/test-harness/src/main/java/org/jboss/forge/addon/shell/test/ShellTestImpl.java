/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.furnace.exception.ContainerException;
import org.jboss.forge.furnace.util.Assert;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Singleton
public class ShellTestImpl implements ShellTest
{
//   private TestShellConfiguration config;
   private TestCommandListener listener;
   private TestAeshSettingsProvider provider;

   @Inject
   public ShellTestImpl(TestAeshSettingsProvider provider, TestCommandListener listener)
   {
      this.listener = listener;
      this.provider = provider;
   }

   @Override
   public Result execute(String line)
   {
      Assert.notNull(line, "Line to execute cannot be null.");

      Result result;
      try
      {
         if (!line.trim().endsWith("\n"))
            line = line + "\n";
         listener.reset();
         provider.getStdIn().write(line.getBytes());
         while (!listener.isExecuted())
         {
            Thread.sleep(10);
         }
         result = listener.getResult();
      }
      catch (Exception e)
      {
         throw new RuntimeException("Failed to execute command.", e);
      }
      return result;
   }

   @Override
   public Result execute(String line, int quantity, TimeUnit unit)
   {
      Assert.notNull(line, "Line to execute cannot be null.");

      Result result;
      try
      {
         if (!line.trim().endsWith("\n"))
            line = line + "\n";
         listener.reset();
         provider.getStdIn().write(line.getBytes());
         long start = System.currentTimeMillis();
         while (!listener.isExecuted())
         {
            if (System.currentTimeMillis() > (start + TimeUnit.MILLISECONDS.convert(quantity, unit)))
            {
               throw new TimeoutException("Timeout expired waiting for command [" + line + "].");
            }

            try
            {
               Thread.sleep(10);
            }
            catch (InterruptedException e)
            {
               throw new ContainerException("Command [" + line + "] did not respond.", e);
            }
         }
         result = listener.getResult();
      }
      catch (Exception e)
      {
         throw new RuntimeException("Failed to execute command.", e);
      }
      return result;
   };

   @Override
   public void waitForStdOut(String value, int quantity, TimeUnit unit) throws TimeoutException
   {
      waitForStream(value, provider.getStdOut(), quantity, unit);
   }

   @Override
   public void waitForStdErr(String value, int quantity, TimeUnit unit) throws TimeoutException
   {
      waitForStream(value, provider.getStdErr(), quantity, unit);
   }

   private void waitForStream(String value, ByteArrayOutputStream stream, int quantity, TimeUnit unit)
            throws TimeoutException
   {
      stream.reset();
      try
      {
         provider.getStdIn().write(value.getBytes());
         provider.getStdIn().flush();
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }

      long start = System.currentTimeMillis();
      while (stream.toByteArray().length == 0)
      {
         if (System.currentTimeMillis() > (start + TimeUnit.MILLISECONDS.convert(quantity, unit)))
         {
            throw new TimeoutException("Timeout expired waiting for shell to respond [" + stream + "].");
         }

         try
         {
            Thread.sleep(10);
         }
         catch (InterruptedException e)
         {
            throw new ContainerException("Stream [" + stream + "] did not respond.", e);
         }
      }
   }

   @Override
   public OutputStream getStdIn()
   {
      return provider.getStdIn();
   }

   @Override
   public String getStdOut()
   {
      return provider.getStdOut().toString();
   }

   @Override
   public String getStdErr()
   {
      return provider.getStdErr().toString();
   }

}
