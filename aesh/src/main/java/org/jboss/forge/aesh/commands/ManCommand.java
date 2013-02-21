/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.aesh.commands;

import java.net.URL;

import javax.inject.Inject;

import org.jboss.aesh.complete.CompleteOperation;
import org.jboss.aesh.complete.Completion;
import org.jboss.aesh.console.Config;
import org.jboss.aesh.console.Console;
import org.jboss.aesh.extensions.manual.Man;
import org.jboss.aesh.util.Parser;
import org.jboss.forge.aesh.ShellContext;
import org.jboss.forge.container.AddonRegistry;
import org.jboss.forge.container.services.ExportedInstance;
import org.jboss.forge.ui.UIBuilder;
import org.jboss.forge.ui.UICommand;
import org.jboss.forge.ui.UICommandMetadata;
import org.jboss.forge.ui.context.UIContext;
import org.jboss.forge.ui.context.UIValidationContext;
import org.jboss.forge.ui.input.UIInputMany;
import org.jboss.forge.ui.result.Result;
import org.jboss.forge.ui.result.Results;
import org.jboss.forge.ui.util.Metadata;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public class ManCommand implements UICommand, Completion
{

   private AddonRegistry registry;

   @Inject
   private UIInputMany<String> arguments;

   @Inject
   public ManCommand(AddonRegistry registry)
   {
      this.registry = registry;
   }

   @Override
   public UICommandMetadata getMetadata()
   {
      return Metadata.forCommand(getClass()).name("man")
               .description("man - an interface to the online reference manuals");
   }

   @Override
   public boolean isEnabled(UIContext context)
   {
      return true;
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      arguments.setLabel("");
      arguments.setRequired(false);
      builder.add(arguments);
   }

   @Override
   public void validate(UIValidationContext validator)
   {

   }

   @Override
   public Result execute(UIContext context) throws Exception
   {
      if (arguments.getValue() != null &&
               context instanceof ShellContext)
      {
         Console console = ((ShellContext) context).getShell().getConsole();
         try
         {
            Man man = new Man(console);
            // for now we only try to display the first
            String commandName = arguments.getValue().iterator().next();
            URL docUrl = getCommand(commandName);
            if (docUrl != null)
            {
               man.setFile(docUrl.openStream(), docUrl.getPath());
               man.attach(((ShellContext) context).getConsoleOutput());
            }
            else
               console.pushToStdOut("No manual page found for: " + commandName + Config.getLineSeparator());

         }
         catch (Exception ioe)
         {
            return Results.fail(ioe.getMessage());
         }
      }
      return null;
   }

   @Override
   public void complete(CompleteOperation completeOperation)
   {
      try
      {
         // list all commands
         if (completeOperation.getBuffer().trim().equals("man"))
         {
            for (ExportedInstance<UICommand> instance : registry.getExportedInstances(UICommand.class))
            {
               completeOperation.addCompletionCandidate(instance.get().getMetadata().getName());
            }
         }
         // find the last
         else
         {
            String item = Parser.findEscapedSpaceWordCloseToEnd(completeOperation.getBuffer().trim());
            completeOperation.setOffset(completeOperation.getCursor() -
                     item.length());
            for (ExportedInstance<UICommand> instance : registry.getExportedInstances(UICommand.class))
            {
               if (instance.get().getMetadata().getName().startsWith(item))
                  completeOperation.addCompletionCandidate(instance.get().getMetadata().getName());
            }
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   private URL getCommand(String name)
   {
      for (ExportedInstance<UICommand> instance : registry.getExportedInstances(UICommand.class))
      {
         if (instance.get().getMetadata().getName().equals(name))
            return instance.get().getMetadata().getDocLocation();
      }
      return null;
   }
}
