/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.test.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.lang.reflect.Field;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.aesh.console.Buffer;
import org.jboss.aesh.console.Console;
import org.jboss.aesh.console.settings.Settings;
import org.jboss.aesh.console.settings.SettingsBuilder;
import org.jboss.aesh.edit.KeyOperation;
import org.jboss.aesh.edit.actions.Operation;
import org.jboss.aesh.terminal.Key;
import org.jboss.aesh.terminal.TestTerminal;
import org.jboss.forge.addon.shell.Shell;
import org.jboss.forge.addon.shell.ShellFactory;
import org.jboss.forge.addon.shell.test.ShellTest;
import org.jboss.forge.addon.ui.CommandExecutionListener;
import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.furnace.exception.ContainerException;
import org.jboss.forge.furnace.util.Assert;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Singleton
public class DefaultShellTest implements ShellTest
{
   private TestCommandListener listener = new TestCommandListener();
   private TestStreams provider = new TestStreams();

   @Inject
   private ShellFactory factory;
   private Shell shell;

   @Override
   public Shell getTestShell()
   {
      if (shell == null)
      {
         shell = factory.createShell(new File(""), provider.getSettings());
         shell.addCommandExecutionListener(listener);
      }
      return shell;
   }

   @PostConstruct
   public void init()
   {
      getTestShell();
   }

   @Override
   public Buffer getBuffer()
   {
      try
      {
         Field field = Console.class.getDeclaredField("buffer");
         field.setAccessible(true);
         return (Buffer) field.get(shell.getConsole());
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
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
   public void waitForStdOutChanged(final String value, int quantity, TimeUnit unit) throws TimeoutException
   {
      waitForStream(new Callable<Void>()
      {
         @Override
         public Void call() throws Exception
         {
            provider.getStdIn().write(value.getBytes());
            provider.getStdIn().flush();
            return null;
         }
      }, provider.getStdOut(), quantity, unit);
   }

   @Override
   public void waitForStdErrChanged(final String value, int quantity, TimeUnit unit) throws TimeoutException
   {
      waitForStream(new Callable<Void>()
      {
         @Override
         public Void call() throws Exception
         {
            provider.getStdIn().write(value.getBytes());
            provider.getStdIn().flush();
            return null;
         }
      }, provider.getStdErr(), quantity, unit);
   }

   private void waitForStream(Callable<?> task, ByteArrayOutputStream stream, int quantity, TimeUnit unit)
            throws TimeoutException
   {
      stream.reset();
      final int size = stream.size();
      try
      {
         task.call();
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }

      long start = System.currentTimeMillis();
      while (System.currentTimeMillis() < (start + TimeUnit.MILLISECONDS.convert(quantity, unit)))
      {
         if (System.currentTimeMillis() >= (start + TimeUnit.MILLISECONDS.convert(quantity, unit))
                  && stream.size() == size)
         {
            throw new TimeoutException("Timeout occurred while waiting for stream.");
         }

         try
         {
            Thread.sleep(10);
         }
         catch (InterruptedException e)
         {
            throw new RuntimeException("Interrupted while waiting for Shell to respond.", e);
         }
      }
   }

   @Override
   public String waitForStdOutChanged(Callable<?> callable, int quantity, TimeUnit unit) throws TimeoutException
   {
      waitForStream(callable, provider.getStdOut(), quantity, unit);
      return new String(provider.getStdOut().toByteArray());
   }

   @Override
   public String waitForStdErrChanged(Callable<?> callable, int quantity, TimeUnit unit) throws TimeoutException
   {
      waitForStream(callable, provider.getStdErr(), quantity, unit);
      return new String(provider.getStdErr().toByteArray());
   }

   @Override
   public void waitForBufferChanged(Callable<?> task, int quantity, TimeUnit unit) throws TimeoutException
   {
      final String buffer = getBuffer().getLine();
      try
      {
         task.call();
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }

      long start = System.currentTimeMillis();
      while (System.currentTimeMillis() < (start + TimeUnit.MILLISECONDS.convert(quantity, unit)))
      {
         if (System.currentTimeMillis() >= (start + TimeUnit.MILLISECONDS.convert(quantity, unit))
                  && buffer.equals(getBuffer().getLine().length()))
         {
            throw new TimeoutException("Timeout occurred while waiting for buffer.");
         }

         try
         {
            Thread.sleep(10);
         }
         catch (InterruptedException e)
         {
            throw new RuntimeException("Interrupted while waiting for Shell to respond.", e);
         }
      }
   }

   @Override
   public void waitForBufferValue(Callable<?> task, int quantity, TimeUnit unit, String expected)
            throws TimeoutException
   {
      try
      {
         task.call();
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }

      long start = System.currentTimeMillis();
      while (System.currentTimeMillis() < (start + TimeUnit.MILLISECONDS.convert(quantity, unit))
               && !getBuffer().getLine().equals(expected))
      {
         if (System.currentTimeMillis() >= (start + TimeUnit.MILLISECONDS.convert(quantity, unit))
                  && !getBuffer().getLine().equals(expected))
         {
            throw new TimeoutException("Timeout occurred while waiting for buffer.");
         }

         try
         {
            Thread.sleep(10);
         }
         catch (InterruptedException e)
         {
            throw new RuntimeException("Interrupted while waiting for Shell to respond.", e);
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

   private class TestStreams
   {
      private PipedOutputStream stdin = new PipedOutputStream();
      private ByteArrayOutputStream stdout = new ByteArrayOutputStream();
      private ByteArrayOutputStream stderr = new ByteArrayOutputStream();
      private PipedInputStream inputStream;

      private Settings settings;

      public Settings getSettings()
      {
         try
         {
            inputStream = new PipedInputStream(stdin);
            settings = new SettingsBuilder()
                     .inputStream(inputStream)
                     .outputStream(stdout)
                     .outputStreamError(stderr)
                     .name("test")
                     .logging(true)
                     .terminal(new TestTerminal())
                     .create();
            settings.getOperationManager().addOperation(new KeyOperation(Key.ENTER, Operation.NEW_LINE));
         }
         catch (IOException e)
         {
            throw new RuntimeException("Could not configure Shell.", e);
         }
         return settings;
      }

      public synchronized OutputStream getStdIn()
      {
         return stdin;
      }

      public synchronized ByteArrayOutputStream getStdOut()
      {
         return stdout;
      }

      public synchronized ByteArrayOutputStream getStdErr()
      {
         return stderr;
      }

   }

   public class TestCommandListener implements CommandExecutionListener
   {
      Result result;

      @Override
      public void preCommandExecuted(UICommand command, UIContext context)
      {
      }

      @Override
      public void postCommandExecuted(UICommand command, UIContext context, Result result)
      {
         synchronized (this)
         {
            this.result = result;
         }
      }

      public boolean isExecuted()
      {
         synchronized (this)
         {
            return result != null;
         }
      }

      public Result getResult()
      {
         synchronized (this)
         {
            return result;
         }
      }

      public void reset()
      {
         synchronized (this)
         {
            result = null;
         }
      }
   }

   @Override
   public void write(String string) throws IOException
   {
      getStdIn().write(string.getBytes());
   }

   private KeyOperation completeChar = new KeyOperation(Key.CTRL_I, Operation.COMPLETE);

   @Override
   public void sendCompletionSignal() throws IOException
   {
      getStdIn().write(completeChar.getFirstValue());
   }

   @Override
   public void clearScreen() throws IOException
   {
      try
      {
         waitForBufferValue(new Callable<String>()
         {
            @Override
            public String call() throws Exception
            {
               shell.getConsole().clear(true);
               return null;
            }
         }, 10, TimeUnit.SECONDS, "");
      }
      catch (TimeoutException e)
      {
         throw new RuntimeException("Could not clear screen within allotted timeout.", e);
      }
   }
}
