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
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PreDestroy;
import javax.enterprise.inject.Vetoed;

import org.jboss.aesh.console.Config;
import org.jboss.aesh.console.Console;
import org.jboss.aesh.console.Prompt;
import org.jboss.aesh.console.settings.Settings;
import org.jboss.aesh.terminal.CharacterType;
import org.jboss.aesh.terminal.Color;
import org.jboss.aesh.terminal.TerminalCharacter;
import org.jboss.forge.addon.shell.aesh.ForgeConsoleCallback;
import org.jboss.forge.addon.shell.aesh.ShellCommand;
import org.jboss.forge.addon.shell.aesh.completion.ForgeCommandCompletion;
import org.jboss.forge.addon.shell.aesh.completion.ForgeOptionCompletion;
import org.jboss.forge.addon.shell.ui.ShellContextImpl;
import org.jboss.forge.addon.ui.CommandExecutionListener;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Selections;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.spi.ListenerRegistration;

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
   private UISelection<?> selection;
   private CommandManager commandManager;

   public ShellImpl(CommandManager commandManager, Settings settings)
   {
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
      console.addCompletion(new ForgeCommandCompletion(this));
      console.addCompletion(new ForgeOptionCompletion(this));
      console.setConsoleCallback(new ForgeConsoleCallback(this));
      try
      {
         console.setPrompt(createInitialPrompt());
      }
      catch (IOException io)
      {
         throw new RuntimeException("Prompt unavailable", io);
      }
      try
      {
         console.start();
      }
      catch (IOException io)
      {
         throw new RuntimeException("Unable to start console", io);
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
   private Prompt createInitialPrompt()
   {
      List<TerminalCharacter> chars = new ArrayList<TerminalCharacter>();
      chars.add(new TerminalCharacter('[', Color.DEFAULT_BG, Color.BLUE_TEXT));
      chars.add(new TerminalCharacter('f', Color.DEFAULT_BG, Color.RED_TEXT,
               CharacterType.BOLD));
      chars.add(new TerminalCharacter('o', Color.DEFAULT_BG, Color.RED_TEXT,
               CharacterType.BOLD));
      chars.add(new TerminalCharacter('r', Color.DEFAULT_BG, Color.RED_TEXT,
               CharacterType.BOLD));
      chars.add(new TerminalCharacter('g', Color.DEFAULT_BG, Color.RED_TEXT,
               CharacterType.BOLD));
      chars.add(new TerminalCharacter('e', Color.DEFAULT_BG, Color.RED_TEXT,
               CharacterType.BOLD));
      chars.add(new TerminalCharacter(']', Color.DEFAULT_BG, Color.BLUE_TEXT,
               CharacterType.PLAIN));
      chars.add(new TerminalCharacter('$', Color.DEFAULT_BG, Color.DEFAULT_TEXT));
      chars.add(new TerminalCharacter(' ', Color.DEFAULT_BG, Color.DEFAULT_TEXT));
      return new Prompt(chars);
   }

   @Override
   public void setCurrentSelection(UISelection<?> selection)
   {
      this.selection = selection;
   }

   public Map<String, ShellCommand> getEnabledShellCommands()
   {
      ShellContextImpl context = new ShellContextImpl(this, getCurrentSelection());
      return commandManager.getEnabledShellCommands(context);
   }

   /**
    * Used in {@link ForgeOptionCompletion} and {@link ForgeConsoleCallback}
    */
   public ShellCommand findCommand(String line)
   {
      String[] tokens = line.split(" ");
      if (tokens.length >= 1)
      {
         return getEnabledShellCommands().get(tokens[0]);
      }
      return null;
   }

   /**
    * Use {@link ForgeCommandCompletion}
    */
   public Iterable<ShellCommand> findMatchingCommands(String line)
   {
      List<ShellCommand> result = new ArrayList<ShellCommand>();
      Map<String, ShellCommand> commandMap = getEnabledShellCommands();

      String[] tokens = line == null ? new String[0] : line.split(" ");
      if (tokens.length <= 1)
      {
         String token = (tokens.length == 1) ? tokens[0] : null;
         for (Entry<String, ShellCommand> entry : commandMap.entrySet())
         {
            if (token == null || entry.getKey().startsWith(token))
               result.add(entry.getValue());
         }
      }
      return result;
   }

   public Result execute(ShellCommand shellCommand)
   {
      // TODO: Fire pre/post listeners
      try
      {
         Result result = shellCommand.execute();
         if (result != null && result.getMessage() != null)
         {
            getConsole().pushToStdOut(result.getMessage() + Config.getLineSeparator());
         }
         return result;
      }
      catch (Exception e)
      {
         return Results.fail(e.getMessage(), e);
      }
   }

   @Override
   public UISelection<?> getCurrentSelection()
   {
      return selection != null ? selection : Selections.emptySelection();
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

}
