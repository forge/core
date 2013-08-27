/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PreDestroy;
import javax.enterprise.inject.Vetoed;

import org.jboss.aesh.console.Console;
import org.jboss.aesh.console.ConsoleOperation;
import org.jboss.aesh.console.Prompt;
import org.jboss.aesh.console.settings.Settings;
import org.jboss.aesh.terminal.CharacterType;
import org.jboss.aesh.terminal.Color;
import org.jboss.aesh.terminal.TerminalCharacter;
import org.jboss.aesh.terminal.TerminalString;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.shell.aesh.AbstractShellInteraction;
import org.jboss.forge.addon.shell.aesh.ForgeConsoleCallback;
import org.jboss.forge.addon.shell.aesh.completion.ForgeCompletion;
import org.jboss.forge.addon.shell.ui.ShellContext;
import org.jboss.forge.addon.shell.ui.ShellContextImpl;
import org.jboss.forge.addon.ui.CommandExecutionListener;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.furnace.addons.AddonRegistry;
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
public class ShellImpl implements Shell
{
   private static final Logger log = Logger.getLogger(ShellImpl.class.getName());

   private final List<CommandExecutionListener> listeners = new ArrayList<CommandExecutionListener>();

   private Console console;
   private FileResource<?> currentResource;
   private CommandManager commandManager;

   public ShellImpl(FileResource<?> initialResource, CommandManager commandManager, Settings settings)
   {
      this.currentResource = initialResource;
      this.commandManager = commandManager;
      init(settings);
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

   void init(Settings settings)
   {
      if (console != null)
      {
         try
         {
            console.stop();
         }
         catch (IOException e)
         {
            log.log(Level.WARNING, "Error while closing previous console", e);
         }
         console = null;
      }
      console = new Console(settings);
      console.addCompletion(new ForgeCompletion(this));
      console.setConsoleCallback(new ForgeConsoleCallback(this));
      updatePrompt();
      try
      {
         console.start();
      }
      catch (IOException io)
      {
         throw new RuntimeException("Unable to start console", io);
      }
   }

   private void updatePrompt()
   {
      try
      {
         console.setPrompt(createPrompt());
      }
      catch (IOException io)
      {
         throw new RuntimeException("Prompt unavailable", io);
      }
   }

   @Override
   public Console getConsole()
   {
      if (console == null)
      {
         throw new IllegalStateException("Console not set. Shell.init not yet called?");
      }
      return console;
   }

   /**
    * Creates an initial prompt
    */
   private Prompt createPrompt()
   {
      // [ currentdir]$
      StringBuilder sb = new StringBuilder();
      sb.append(new TerminalCharacter('[', Color.DEFAULT_BG, Color.BLUE_TEXT, CharacterType.BOLD));
      sb.append(new TerminalString(currentResource.getName(), Color.DEFAULT_BG, Color.RED_TEXT, CharacterType.PLAIN));
      sb.append(new TerminalCharacter(']', Color.DEFAULT_BG, Color.BLUE_TEXT, CharacterType.BOLD));
      sb.append(new TerminalCharacter('$', Color.DEFAULT_BG, Color.DEFAULT_TEXT));
      sb.append(new TerminalCharacter(' ', Color.DEFAULT_BG, Color.DEFAULT_TEXT));
      return new Prompt(sb.toString());
   }

   public Map<String, AbstractShellInteraction> getEnabledShellCommands(ShellContext context)
   {
      return commandManager.getEnabledShellCommands(context);
   }

   /**
    * Used in {@link ForgeCompletion} and {@link ForgeConsoleCallback}
    */
   public AbstractShellInteraction findCommand(ShellContext shellContext, String line)
   {
      String[] tokens = line.split(" ");
      if (tokens.length >= 1)
      {
         return getEnabledShellCommands(shellContext).get(tokens[0]);
      }
      return null;
   }

   public Collection<AbstractShellInteraction> findMatchingCommands(ShellContext shellContext, String line)
   {
      Set<AbstractShellInteraction> result = new TreeSet<AbstractShellInteraction>();

      String[] tokens = line == null ? new String[0] : line.split(" ");
      if (tokens.length <= 1)
      {
         Map<String, AbstractShellInteraction> commandMap = getEnabledShellCommands(shellContext);
         String token = (tokens.length == 1) ? tokens[0] : null;
         for (Entry<String, AbstractShellInteraction> entry : commandMap.entrySet())
         {
            if (token == null || entry.getKey().startsWith(token))
               result.add(entry.getValue());
         }
      }
      return result;
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

   public ShellContext newShellContext(ConsoleOperation consoleOperation)
   {
      ShellContextImpl shellContextImpl = new ShellContextImpl(this, currentResource);
      shellContextImpl.setConsoleOperation(consoleOperation);
      return shellContextImpl;
   }

   @PreDestroy
   @Override
   public void close()
   {
      try
      {
         this.console.stop();
      }
      catch (Exception ignored)
      {
         // Exception is ignored
      }
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

}
