/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.test.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.aesh.console.AeshConsoleImpl;
import org.jboss.aesh.console.settings.Settings;
import org.jboss.aesh.console.settings.SettingsBuilder;
import org.jboss.aesh.edit.KeyOperation;
import org.jboss.aesh.edit.actions.Action;
import org.jboss.aesh.edit.actions.Operation;
import org.jboss.aesh.terminal.Key;
import org.jboss.aesh.terminal.TestTerminal;
import org.jboss.forge.addon.shell.Shell;
import org.jboss.forge.addon.shell.ShellFactory;
import org.jboss.forge.addon.shell.test.ShellTest;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.furnace.container.cdi.events.Local;
import org.jboss.forge.furnace.event.PreShutdown;
import org.jboss.forge.furnace.exception.ContainerException;
import org.jboss.forge.furnace.util.Assert;
import org.jboss.forge.furnace.util.Callables;
import org.jboss.forge.furnace.util.OperatingSystemUtils;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class DefaultShellTest implements ShellTest
{
   private static final String LINE_SEPARATOR = OperatingSystemUtils.getLineSeparator();
   private static final KeyOperation completeChar = new KeyOperation(Key.CTRL_I, Operation.COMPLETE, Action.COMPLETE);
   private final TestCommandListener listener = new TestCommandListener();
   private final TestStreams provider = new TestStreams();

   @Inject
   private ShellFactory factory;
   private Shell shell;

   private final Callable<?> nullCallable = Callables.returning(null);

   @Override
   public Shell getShell()
   {
      if (shell == null)
      {
         shell = factory.createShell(OperatingSystemUtils.getTempDirectory(), provider.getSettings());
         shell.addCommandExecutionListener(listener);
      }
      return shell;
   }

   @PostConstruct
   public void init()
   {
      getShell();
   }

   public void teardown(@Observes @Local PreShutdown event) throws Exception
   {
      close();
   }

   @PreDestroy
   @Override
   public void close()
   {
      if (shell != null)
      {
         try
         {
            shell.close();
         }
         catch (Exception e)
         {
            e.printStackTrace();
         }
         shell = null;
      }
   }

   @Override
   public String getBuffer()
   {
      AeshConsoleImpl console = (AeshConsoleImpl) getShell().getConsole();
      return console.getBuffer();
   }

   @Override
   public void execute(String line)
   {
      Assert.notNull(line, "Line to execute cannot be null.");

      try
      {
         if (!line.endsWith(LINE_SEPARATOR))
            line = line + LINE_SEPARATOR;
         provider.getStdIn().write(line.getBytes());
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }

   @Override
   public Result execute(String line, int quantity, TimeUnit unit) throws TimeoutException
   {
      Assert.notNull(line, "Line to execute cannot be null.");

      Result result;
      try
      {
         if (!line.endsWith(LINE_SEPARATOR))
            line = line + LINE_SEPARATOR;
         listener.reset();
         provider.getStdIn().write(line.getBytes());
         long start = System.currentTimeMillis();
         while (!listener.isExecuted())
         {
            if (System.currentTimeMillis() > (start + TimeUnit.MILLISECONDS.convert(quantity, unit)))
            {
               throwTimeout("Timeout expired waiting for command [" + line + "] to execute.");
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
      catch (IOException e)
      {
         throw new RuntimeException("Failed to execute command.", e);
      }
      return result;
   }

   @Override
   public void waitForStdOutChanged(final String value, int quantity, TimeUnit unit) throws TimeoutException
   {
      waitForStreamChanged(new Callable<Void>()
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
      waitForStreamChanged(new Callable<Void>()
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

   @Override
   public String waitForStdOutChanged(Callable<?> task, int quantity, TimeUnit unit) throws TimeoutException
   {
      waitForStreamChanged(task, provider.getStdOut(), quantity, unit);
      return new String(provider.getStdOut().toByteArray());
   }

   @Override
   public String waitForStdErrChanged(Callable<?> task, int quantity, TimeUnit unit) throws TimeoutException
   {
      waitForStreamChanged(task, provider.getStdErr(), quantity, unit);
      return new String(provider.getStdErr().toByteArray());
   }

   @Override
   public void waitForStdOutValue(String expected, int timeout, TimeUnit unit) throws TimeoutException
   {
      waitForStreamValue(nullCallable, provider.getStdOut(), expected, timeout, unit);
   }

   @Override
   public void waitForStdErrValue(String expected, int timeout, TimeUnit unit) throws TimeoutException
   {
      waitForStreamValue(nullCallable, provider.getStdErr(), expected, timeout, unit);
   }

   @Override
   public void waitForStdOutValue(Callable<Void> task, String expected, int timeout, TimeUnit unit)
            throws TimeoutException
   {
      clearAndWaitForStreamValue(task, provider.getStdOut(), expected, timeout, unit);
   }

   @Override
   public void waitForStdErrValue(Callable<Void> task, String expected, int timeout, TimeUnit unit)
            throws TimeoutException
   {
      clearAndWaitForStreamValue(task, provider.getStdErr(), expected, timeout, unit);
   }

   private void waitForStreamChanged(Callable<?> task, ByteArrayOutputStream stream, int quantity, TimeUnit unit)
            throws TimeoutException
   {
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
      while (stream.size() == size)
      {
         if (System.currentTimeMillis() >= (start + TimeUnit.MILLISECONDS.convert(quantity, unit))
                  && stream.size() == size)
         {
            throwTimeout("Timeout occurred while waiting for stream to be written.");
         }

         try
         {
            Thread.sleep(10);
         }
         catch (InterruptedException e)
         {
            throw new RuntimeException("Interrupted while waiting for stream to be written.", e);
         }
      }
   }

   private void clearAndWaitForStreamValue(Callable<?> task, ByteArrayOutputStream stream, String expected,
            int quantity, TimeUnit unit) throws TimeoutException
   {
      stream.reset();
      waitForStreamValue(task, stream, expected, quantity, unit);
   }

   private void waitForStreamValue(Callable<?> task, ByteArrayOutputStream stream, String expected, int quantity,
            TimeUnit unit) throws TimeoutException
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
      while (!new String(stream.toByteArray()).contains(expected))
      {
         if (System.currentTimeMillis() >= (start + TimeUnit.MILLISECONDS.convert(quantity, unit))
                  && !new String(stream.toByteArray()).contains(expected))
         {
            throwTimeout("Timeout occurred while waiting for stream value [" + expected + "].");
         }

         try
         {
            Thread.sleep(10);
         }
         catch (InterruptedException e)
         {
            throw new RuntimeException("Interrupted while waiting for stream value [" + expected + "].", e);
         }
      }
   }

   @Override
   public void waitForBufferChanged(Callable<?> task, int quantity, TimeUnit unit) throws TimeoutException
   {
      final String buffer = getBuffer();
      try
      {
         task.call();
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }

      long start = System.currentTimeMillis();
      while (buffer.equals(getBuffer().length()))
      {
         if (System.currentTimeMillis() >= (start + TimeUnit.MILLISECONDS.convert(quantity, unit))
                  && buffer.equals(getBuffer()))
         {
            throwTimeout("Timeout occurred while waiting for buffer value to change from [" + buffer
                     + "].");
         }

         try
         {
            Thread.sleep(10);
         }
         catch (InterruptedException e)
         {
            throw new RuntimeException("Interrupted while waiting for buffer value to change from [" + buffer + "].",
                     e);
         }
      }
   }

   @Override
   public void waitForBufferValue(Callable<?> task, String expected, int quantity, TimeUnit unit)
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
      while (!getBuffer().equals(expected))
      {
         if (System.currentTimeMillis() >= (start + TimeUnit.MILLISECONDS.convert(quantity, unit))
                  && !getBuffer().equals(expected))
         {
            throwTimeout("Timeout occurred while waiting for buffer to equal value [" + expected + "].");
         }

         try
         {
            Thread.sleep(10);
         }
         catch (InterruptedException e)
         {
            throw new RuntimeException("Interrupted while waiting for buffer to equal value  [" + expected + "].", e);
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
      private final PipedOutputStream stdin = new PipedOutputStream();
      private final ByteArrayOutputStream stdout = new ByteArrayOutputStream();
      private final ByteArrayOutputStream stderr = new ByteArrayOutputStream();
      private PipedInputStream inputStream;

      private Settings settings;

      public Settings getSettings()
      {
         try
         {
            inputStream = new PipedInputStream(stdin);
            settings = new SettingsBuilder()
                     .inputStream(inputStream)
                     .outputStream(new PrintStream(stdout, true))
                     .outputStreamError(new PrintStream(stderr, true))
                     .name("test")
                     .logging(true)
                     .setExportUsesSystemEnvironment(true)
                     .enableExport(true)
                     .ansi(false)
                     .terminal(new TestTerminal())
                     .create();
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

   @Override
   public void write(String string) throws IOException
   {
      getStdIn().write(string.getBytes());
   }

   @Override
   public void sendCompletionSignal() throws IOException
   {
      getStdIn().write(completeChar.getFirstValue());
   }

   private void throwTimeout(String message) throws TimeoutException
   {
      throw new TimeoutException(message
               + LINE_SEPARATOR
               + LINE_SEPARATOR
               + "STDOUT: " + provider.getStdOut().toString()
               + LINE_SEPARATOR
               + LINE_SEPARATOR
               + "STDERR: " + provider.getStdErr().toString()
               + LINE_SEPARATOR
               + LINE_SEPARATOR
               + "BUFFER: [" + getBuffer() + "]"
               + LINE_SEPARATOR);
   };

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
               AeshConsoleImpl console = (AeshConsoleImpl) getShell().getConsole();
               console.getInputProcessor().resetBuffer();
               provider.getStdOut().reset();
               provider.getStdErr().reset();
               return null;
            }
         }, "", 10, TimeUnit.SECONDS);
      }
      catch (TimeoutException e)
      {
         throw new RuntimeException("Could not clear screen within allotted timeout.", e);
      }
   }

   @Override
   public String waitForCompletion(final String expected, final String write, final int quantity, final TimeUnit unit)
            throws TimeoutException
   {
      waitForStdOutValue(new Callable<Void>()
      {
         @Override
         public Void call() throws Exception
         {
            waitForBufferValue(new Callable<String>()
            {
               @Override
               public String call() throws Exception
               {
                  write(write);
                  sendCompletionSignal();
                  return null;
               }
            }, expected, quantity, unit);
            return null;
         }
      }, expected, quantity, unit);

      return getStdOut();
   }

   @Override
   public String waitForCompletion(final int quantity, final TimeUnit unit) throws TimeoutException
   {
      final String buffer = getBuffer();
      waitForStdOutValue(new Callable<Void>()
      {
         @Override
         public Void call() throws Exception
         {
            waitForBufferValue(new Callable<String>()
            {
               @Override
               public String call() throws Exception
               {
                  sendCompletionSignal();
                  return null;
               }
            }, buffer, quantity, unit);
            return null;
         }
      }, buffer, quantity, unit);

      return getStdOut();
   }
}
