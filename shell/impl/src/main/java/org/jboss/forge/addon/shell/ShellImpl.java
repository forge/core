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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.aesh.console.Console;
import org.jboss.aesh.console.Prompt;
import org.jboss.aesh.console.settings.Settings;
import org.jboss.aesh.terminal.CharacterType;
import org.jboss.aesh.terminal.Color;
import org.jboss.aesh.terminal.TerminalCharacter;
import org.jboss.forge.addon.shell.aesh.ForgeConsoleCallback;
import org.jboss.forge.addon.ui.CommandExecutionListener;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.spi.ListenerRegistration;

/**
 * Implementation of the {@link Shell} interface.
 * 
 * Use the {@link AddonRegistry#getServices(Class)} to retrieve an instance of this object
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ShellImpl implements Shell
{
   private static final Logger log = Logger.getLogger(ShellImpl.class.getName());

   private final AddonRegistry addonRegistry;

   private final List<CommandExecutionListener> listeners = new ArrayList<CommandExecutionListener>();

   private Console console;

   private UISelection<?> initialSelection;

   public ShellImpl(AddonRegistry addonRegistry, Settings settings)
   {
      this.addonRegistry = addonRegistry;
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
      this.console = new Console(settings);
      this.console.setConsoleCallback(new ForgeConsoleCallback(this, addonRegistry));
      try
      {
         this.console.setPrompt(createInitialPrompt());
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
   public void setInitialSelection(UISelection<?> initialSelection)
   {
      this.initialSelection = initialSelection;
   }

}
