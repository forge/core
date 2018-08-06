/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.script.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.script.AbstractScriptEngine;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;

import org.jboss.aesh.console.Prompt;
import org.jboss.aesh.console.settings.Settings;
import org.jboss.aesh.console.settings.SettingsBuilder;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.script.ScriptContextBuilder;
import org.jboss.forge.addon.shell.CommandNotFoundListener;
import org.jboss.forge.addon.shell.Shell;
import org.jboss.forge.addon.shell.ShellFactory;
import org.jboss.forge.addon.ui.command.CommandExecutionListener;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.exception.ContainerException;
import org.jboss.forge.furnace.spi.ListenerRegistration;
import org.jboss.forge.furnace.util.Assert;
import org.jboss.forge.furnace.util.OperatingSystemUtils;

/**
 * {@link ScriptEngine} implementation for JBoss Forge
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
class ForgeScriptEngine extends AbstractScriptEngine
{
   private final ForgeScriptEngineFactory factory;
   private final ShellFactory shellFactory;
   private final ResourceFactory resourceFactory;

   ForgeScriptEngine(ForgeScriptEngineFactory factory)
   {
      this.factory = factory;

      AddonRegistry addonRegistry = Furnace.instance(getClass().getClassLoader()).getAddonRegistry();
      this.shellFactory = addonRegistry.getServices(ShellFactory.class).get();
      this.resourceFactory = addonRegistry.getServices(ResourceFactory.class).get();
   }

   @Override
   public Object eval(String script, ScriptContext context) throws ScriptException
   {
      return eval(new StringReader(script), context);
   }

   @Override
   public Object eval(Reader reader, ScriptContext context) throws ScriptException
   {
      Result result = null;
      // Get STDOUT
      PrintStream stdout = (PrintStream) context.getAttribute(ScriptContextBuilder.OUTPUT_PRINTSTREAM_ATTRIBUTE);
      if (stdout == null)
      {
         stdout = System.out;
      }
      stdout = new UncloseablePrintStream(stdout);

      // Get STDERR
      PrintStream stderr = (PrintStream) context.getAttribute(ScriptContextBuilder.ERROR_PRINTSTREAM_ATTRIBUTE);
      if (stderr == null)
      {
         stderr = System.err;
      }
      stderr = new UncloseablePrintStream(stderr);

      // Get Current resource
      Resource<?> currentResource = (Resource<?>) context.getAttribute(ScriptContextBuilder.CURRENT_RESOURCE_ATTRIBUTE);
      if (currentResource == null)
      {
         currentResource = resourceFactory.create(OperatingSystemUtils.getUserHomeDir());
      }
      Integer timeoutValue = (Integer) context.getAttribute(ScriptContextBuilder.TIMEOUT_ATTRIBUTE);
      if (timeoutValue == null)
      {
         timeoutValue = 500;
      }

      PipedOutputStream stdin = new PipedOutputStream();
      BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stdin));
      try
      {
         Settings settings = new SettingsBuilder()
                  .inputStream(new PipedInputStream(stdin))
                  .outputStream(stdout)
                  .outputStreamError(stderr)
                  .create();
         try (Shell scriptShell = shellFactory.createShell(currentResource, settings))
         {
            scriptShell.getConsole().setPrompt(new Prompt(""));
            try (BufferedReader lineReader = new BufferedReader(reader))
            {
               long startTime = System.currentTimeMillis();
               String line;
               while ((line = readLine(lineReader)) != null)
               {
                  try
                  {
                     if (skipsLine(line))
                     {
                        // Skip Comments
                        continue;
                     }
                     result = execute(scriptShell, writer, line, timeoutValue,
                              TimeUnit.SECONDS, startTime);

                     if (result instanceof Failed)
                     {
                        break;
                     }
                     else
                     {
                        currentResource = scriptShell.getCurrentResource();
                     }
                  }
                  catch (TimeoutException e)
                  {
                     result = Results.fail("Execution timed out.", e);
                     break;
                  }
               }
            }
         }
         context.setAttribute(ScriptContextBuilder.CURRENT_RESOURCE_ATTRIBUTE, currentResource,
                  ScriptContext.ENGINE_SCOPE);
      }
      catch (Exception io)
      {
         throw new ScriptException(io);
      }
      return result;
   }

   @Override
   public Bindings createBindings()
   {
      // Not needed
      return null;
   }

   @Override
   public ScriptEngineFactory getFactory()
   {
      return factory;
   }

   private String readLine(BufferedReader reader) throws IOException
   {
      StringBuilder result = new StringBuilder();
      String line;
      boolean read = false;
      while ((line = reader.readLine()) != null)
      {
         read = true;
         line = line.trim();
         if (line.endsWith("\\"))
         {
            // Read next line
            result.append(line.substring(0, line.lastIndexOf("\\"))).append(' ');
         }
         else
         {
            result.append(line);
            break;
         }
      }
      return result.length() == 0 && !read ? null : result.toString();
   }

   private Result execute(Shell shell, BufferedWriter stdin, String line, int quantity, TimeUnit unit, long startTime)
            throws TimeoutException
   {
      Assert.notNull(line, "Line to execute cannot be null.");
      if (skipsLine(line))
      {
         return Results.success();
      }
      Result result = null;

      if (!line.trim().isEmpty())
      {
         if (!line.endsWith(OperatingSystemUtils.getLineSeparator()))
            line = line + OperatingSystemUtils.getLineSeparator();

         ScriptCommandListener listener = new ScriptCommandListener();
         ListenerRegistration<CommandExecutionListener> listenerRegistration = shell
                  .addCommandExecutionListener(listener);
         ListenerRegistration<CommandNotFoundListener> notFoundRegistration = shell
                  .addCommandNotFoundListener(listener);
         try
         {
            stdin.write(line);
            stdin.flush();
            while (!listener.isExecuted())
            {
               if (System.currentTimeMillis() > (startTime + TimeUnit.MILLISECONDS.convert(quantity, unit)))
               {
                  throw new TimeoutException("Timeout expired waiting for command [" + line + "] to execute.");
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
         finally
         {
            listenerRegistration.removeListener();
            notFoundRegistration.removeListener();
         }
      }
      return result;
   }

   private boolean skipsLine(String line)
   {
      return line.startsWith("#") || line.trim().isEmpty();
   }

}
