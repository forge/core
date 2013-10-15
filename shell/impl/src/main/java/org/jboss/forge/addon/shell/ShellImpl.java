/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.PreDestroy;
import javax.enterprise.inject.Vetoed;

import org.jboss.aesh.cl.parser.CommandLineParser;
import org.jboss.aesh.console.AeshConsole;
import org.jboss.aesh.console.AeshConsoleBuilder;
import org.jboss.aesh.console.Prompt;
import org.jboss.aesh.console.command.AeshCommandContainer;
import org.jboss.aesh.console.command.Command;
import org.jboss.aesh.console.command.CommandContainer;
import org.jboss.aesh.console.command.CommandInvocation;
import org.jboss.aesh.console.command.CommandNotFoundException;
import org.jboss.aesh.console.command.CommandRegistry;
import org.jboss.aesh.console.command.CommandResult;
import org.jboss.aesh.console.settings.Settings;
import org.jboss.aesh.terminal.CharacterType;
import org.jboss.aesh.terminal.Color;
import org.jboss.aesh.terminal.TerminalCharacter;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.shell.aesh.AbstractShellInteraction;
import org.jboss.forge.addon.shell.ui.ShellContext;
import org.jboss.forge.addon.shell.ui.ShellContextImpl;
import org.jboss.forge.addon.shell.ui.ShellUIOutputImpl;
import org.jboss.forge.addon.shell.ui.ShellValidationContext;
import org.jboss.forge.addon.ui.CommandExecutionListener;
import org.jboss.forge.addon.ui.context.UIContextListener;
import org.jboss.forge.addon.ui.output.UIOutput;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.services.Imported;
import org.jboss.forge.furnace.spi.ListenerRegistration;
import org.jboss.forge.furnace.util.Assert;

/**
 * Implementation of the {@link Shell} interface.
 * 
 * Use the {@link AddonRegistry#getServices(Class)} to retrieve an instance of this object
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Vetoed
public class ShellImpl implements Shell, CommandRegistry
{
   private final List<CommandExecutionListener> listeners = new ArrayList<CommandExecutionListener>();

   private FileResource<?> currentResource;
   private final CommandManager commandManager;
   private final AddonRegistry addonRegistry;
   private final AeshConsole console;
   private final UIOutput output;

   public ShellImpl(FileResource<?> initialResource, Settings settings, CommandManager commandManager,
            AddonRegistry addonRegistry)
   {
      this.currentResource = initialResource;
      this.addonRegistry = addonRegistry;
      this.commandManager = commandManager;
      console = new AeshConsoleBuilder().prompt(createPrompt()).settings(settings).commandRegistry(this)
               .create();
      output = new ShellUIOutputImpl(console);
      console.start();
   }

   @Override
   public ListenerRegistration<CommandExecutionListener> addCommandExecutionListener(
            final CommandExecutionListener listener)
   {
      listeners.add(listener);
      return new ListenerRegistration<CommandExecutionListener>()
      {
         @Override
         public CommandExecutionListener removeListener()
         {
            listeners.remove(listener);
            return listener;
         }
      };
   }

   private void updatePrompt()
   {
      console.setPrompt(createPrompt());
   }

   /**
    * Creates an initial prompt
    */
   private Prompt createPrompt()
   {
      // [ currentdir]$
      List<TerminalCharacter> prompt = new ArrayList<TerminalCharacter>();
      prompt.add(new TerminalCharacter('[', Color.DEFAULT_BG, Color.BLUE_TEXT, CharacterType.BOLD));
      for (char c : currentResource.getName().toCharArray())
         prompt.add(new TerminalCharacter(c, Color.DEFAULT_BG, Color.RED_TEXT, CharacterType.PLAIN));
      prompt.add(new TerminalCharacter(']', Color.DEFAULT_BG, Color.BLUE_TEXT, CharacterType.BOLD));
      prompt.add(new TerminalCharacter('$', Color.DEFAULT_BG, Color.DEFAULT_TEXT));
      prompt.add(new TerminalCharacter(' ', Color.DEFAULT_BG, Color.DEFAULT_TEXT));
      return new Prompt(prompt);
   }

   public Result execute(AbstractShellInteraction shellCommand)
   {
      Result result = null;
      try
      {
         firePreCommandListeners(shellCommand);
         result = shellCommand.execute();
      }
      catch (Exception e)
      {
         result = Results.fail(e.getMessage(), e);
      }
      finally
      {
         firePostCommandListeners(shellCommand, result);
      }
      return result;
   }

   public ShellContextImpl newShellContext()
   {
      Imported<UIContextListener> listeners = addonRegistry.getServices(UIContextListener.class);
      ShellContextImpl shellContextImpl = new ShellContextImpl(this, currentResource, listeners);
      return shellContextImpl;
   }

   /**
    * @param shellCommand
    */
   private void firePreCommandListeners(AbstractShellInteraction shellCommand)
   {
      for (CommandExecutionListener listener : listeners)
      {
         listener.preCommandExecuted(shellCommand.getSourceCommand(), shellCommand.getContext());
      }
   }

   /**
    * @param shellCommand
    */
   private void firePostCommandListeners(AbstractShellInteraction shellCommand, Result result)
   {
      for (CommandExecutionListener listener : listeners)
      {
         listener.postCommandExecuted(shellCommand.getSourceCommand(), shellCommand.getContext(), result);
      }

   }

   @PreDestroy
   @Override
   public void close()
   {
      this.console.stop();
   }

   public ConverterFactory getConverterFactory()
   {
      return commandManager.getConverterFactory();
   }

   @Override
   public FileResource<?> getCurrentResource()
   {
      return currentResource;
   }

   @Override
   public void setCurrentResource(FileResource<?> resource)
   {
      Assert.notNull(resource, "Current resource should not be null");
      this.currentResource = resource;
      updatePrompt();
   }

   @Override
   public boolean isGUI()
   {
      return false;
   }

   @Override
   public AeshConsole getConsole()
   {
      return console;
   }

   @Override
   public Set<String> getAllCommandNames()
   {
      ShellContextImpl newShellContext = newShellContext();
      try
      {
         return commandManager.getAllCommandNames(newShellContext);
      }
      finally
      {
         newShellContext.destroy();
      }
   }

   @Override
   public CommandContainer getCommand(String name, String completeLine) throws CommandNotFoundException
   {
      ShellContextImpl shellContext = newShellContext();
      AbstractShellInteraction cmd = commandManager.findCommand(shellContext, name);
      if (cmd == null)
         throw new CommandNotFoundException(name);
      try
      {
         CommandLineParser parser = cmd.getParser(shellContext, completeLine);
         return new AeshCommandContainer(parser, new CommandAdapter(cmd));
      }
      catch (Exception e)
      {
         throw new RuntimeException("Error while creating parser: " + e.getMessage(), e);
      }
      finally
      {
         shellContext.destroy();
      }
   }

   @Override
   public UIOutput getOutput()
   {
      return output;
   }

   /**
    * Adapts the current {@link AbstractShellInteraction} to a {@link Command}
    * 
    * @author <a href="ggastald@redhat.com">George Gastaldi</a>
    */
   private class CommandAdapter implements Command<CommandInvocation>
   {
      private final AbstractShellInteraction interaction;

      public CommandAdapter(AbstractShellInteraction interaction)
      {
         super();
         this.interaction = interaction;
      }

      @SuppressWarnings("unchecked")
      @Override
      public CommandResult execute(CommandInvocation commandInvocation) throws IOException
      {
         boolean failure;
         ShellValidationContext validationContext = interaction.validate();
         List<String> errors = validationContext.getErrors();
         if (errors.isEmpty())
         {
            Result result = ShellImpl.this.execute(interaction);
            failure = (result instanceof Failed);
            if (result != null && result.getMessage() != null)
            {
               console.out().println(result.getMessage());
            }
            ShellContext context = interaction.getContext();
            Object selection = context.getSelection();
            if (selection != null)
            {
               if (selection instanceof Iterable<?>)
               {
                  for (FileResource<?> item : (Iterable<FileResource<?>>) selection)
                  {
                     if (item != null)
                     {
                        setCurrentResource(item);
                        break;
                     }
                  }
               }
               else
               {
                  setCurrentResource((FileResource<?>) selection);
               }
            }

         }
         else
         {
            failure = true;
            // Display the error messages
            for (String error : errors)
            {
               console.err().println("**ERROR**: " + error);
            }
         }
         return failure ? CommandResult.FAILURE : CommandResult.SUCCESS;
      }
   }
}
