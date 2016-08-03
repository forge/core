/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.script.ui;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;

import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.script.ScriptContextBuilder;
import org.jboss.forge.addon.script.impl.ForgeScriptEngineFactory;
import org.jboss.forge.addon.shell.ui.AbstractShellCommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.facets.HintsFacet;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponentFactory;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UIInputMany;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.output.UIOutput;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.furnace.util.Streams;

/**
 * Implementation of the "run" command
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class RunCommand extends AbstractShellCommand
{
   private ScriptEngine scriptEngine;

   private UIInput<Integer> timeout;
   private UIInputMany<String> arguments;
   private UIInput<String> command;

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass()).name("run")
               .description("Execute/run a forge script file.");
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      AddonRegistry addonRegistry = SimpleContainer.getFurnace(this.getClass().getClassLoader()).getAddonRegistry();

      InputComponentFactory inputFactory = addonRegistry.getServices(InputComponentFactory.class).get();

      this.timeout = inputFactory.createInput("timeout", Integer.class);
      this.timeout.setDefaultValue(500).setLabel("Timeout (seconds)")
               .setDescription("Set the timeout after which this script should abort if execution has not completed.");

      this.arguments = inputFactory.createInputMany("arguments", 'c', String.class);
      this.arguments.setLabel("Arguments").getFacet(HintsFacet.class).setInputType(InputType.FILE_PICKER);

      this.command = inputFactory.createInput("command", 'c', String.class);

      this.scriptEngine = addonRegistry.getServices(ForgeScriptEngineFactory.class).get().getScriptEngine();

      builder.add(timeout).add(arguments).add(command);
   }

   @Override
   public void validate(UIValidationContext validator)
   {
      if (!command.hasValue() && !arguments.hasValue())
      {
         validator.addValidationError(null, "Command or script file must be provided");
      }
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      Result result = Results.fail("Error executing script.");
      UIContext uiContext = context.getUIContext();
      Resource<?> currentResource = (Resource<?>) uiContext.getInitialSelection().get();
      final UIOutput output = uiContext.getProvider().getOutput();
      if (command.hasValue())
      {
         String[] commands = command.getValue().split(" ");
         ProcessBuilder processBuilder = new ProcessBuilder(commands);
         Object currentDir = currentResource.getUnderlyingResourceObject();
         if (currentDir instanceof File)
         {
            processBuilder.directory((File) currentDir);
         }
         final Process process = processBuilder.start();
         ExecutorService executor = Executors.newFixedThreadPool(2);
         // Read std out
         executor.submit(() -> Streams.write(process.getInputStream(), output.out()));
         // Read std err
         executor.submit(() -> Streams.write(process.getErrorStream(), output.err()));
         executor.shutdown();
         try
         {
            int returnCode = process.waitFor();
            if (returnCode == 0)
            {
               result = Results.success();
            }
            else
            {
               result = Results.fail("Error while executing native command. See output for more details");
            }
         }
         catch (InterruptedException ie)
         {
            result = Results.success("Command execution interrupted");
         }
         finally
         {
            process.destroy();
         }
      }
      else
      {
         Resource<?> selectedResource = currentResource;
         ALL: for (String path : arguments.getValue())
         {
            List<Resource<?>> resources = currentResource.resolveChildren(path);
            for (Resource<?> resource : resources)
            {
               if (resource.exists())
               {
                  ScriptContext scriptContext = ScriptContextBuilder.create().currentResource(currentResource)
                           .stdout(output.out()).stderr(output.err()).timeout(timeout.getValue()).build();
                  result = (Result) scriptEngine.eval(resource.getContents(), scriptContext);
                  selectedResource = (Resource<?>) scriptContext
                           .getAttribute(ScriptContextBuilder.CURRENT_RESOURCE_ATTRIBUTE);
               }
               else
               {
                  result = Results.fail(path + ": not found.");
                  break ALL;
               }
            }
         }
         if (!(result instanceof Failed))
         {
            uiContext.setSelection(selectedResource);
         }
      }
      return result;
   }
}
