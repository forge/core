/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PreDestroy;
import javax.enterprise.inject.Vetoed;

import org.jboss.aesh.console.AeshConsole;
import org.jboss.aesh.console.AeshConsoleBuilder;
import org.jboss.aesh.console.Prompt;
import org.jboss.aesh.console.settings.Settings;
import org.jboss.aesh.terminal.CharacterType;
import org.jboss.aesh.terminal.Color;
import org.jboss.aesh.terminal.TerminalCharacter;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.shell.aesh.AbstractShellInteraction;
import org.jboss.forge.addon.shell.aesh.ForgeCommandRegistry;
import org.jboss.forge.addon.shell.ui.ShellContextImpl;
import org.jboss.forge.addon.shell.ui.ShellUIOutputImpl;
import org.jboss.forge.addon.ui.CommandExecutionListener;
import org.jboss.forge.addon.ui.context.UIContextListener;
import org.jboss.forge.addon.ui.output.UIOutput;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.services.Imported;
import org.jboss.forge.furnace.spi.ListenerRegistration;
import org.jboss.forge.furnace.util.Assert;

/**
 * Implementation of the {@link Shell} interface.
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Vetoed
public class ShellImpl implements Shell
{
   private final List<CommandExecutionListener> listeners = new LinkedList<CommandExecutionListener>();

   private FileResource<?> currentResource;
   private final AddonRegistry addonRegistry;
   private final AeshConsole console;
   private final UIOutput output;

   public ShellImpl(FileResource<?> initialResource, Settings settings, CommandManager commandManager,
            AddonRegistry addonRegistry)
   {
      this.currentResource = initialResource;
      this.addonRegistry = addonRegistry;
      this.console = new AeshConsoleBuilder().prompt(createPrompt()).settings(settings)
               .commandRegistry(new ForgeCommandRegistry(this, commandManager))
               .create();
      this.output = new ShellUIOutputImpl(console);
      this.console.start();
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
         firePostCommandListeners(shellCommand, result);
      }
      catch (Exception e)
      {
         firePostCommandFailureListeners(shellCommand, e);
         e.printStackTrace();
         result = Results.fail(e.getMessage(), e);
      }
      return result;
   }

   public ShellContextImpl newShellContext()
   {
      Imported<UIContextListener> listeners = addonRegistry.getServices(UIContextListener.class);
      ShellContextImpl shellContextImpl = new ShellContextImpl(this, currentResource, listeners);
      return shellContextImpl;
   }

   private void firePreCommandListeners(AbstractShellInteraction shellCommand)
   {
      for (CommandExecutionListener listener : listeners)
      {
         listener.preCommandExecuted(shellCommand.getSourceCommand(), shellCommand.getContext());
      }
   }

   private void firePostCommandListeners(AbstractShellInteraction shellCommand, Result result)
   {
      for (CommandExecutionListener listener : listeners)
      {
         listener.postCommandExecuted(shellCommand.getSourceCommand(), shellCommand.getContext(), result);
      }

   }

   private void firePostCommandFailureListeners(AbstractShellInteraction shellCommand, Throwable failure)
   {
      for (CommandExecutionListener listener : listeners)
      {
         listener.postCommandFailure(shellCommand.getSourceCommand(), shellCommand.getContext(), failure);
      }

   }

   @PreDestroy
   @Override
   public void close()
   {
      this.console.stop();
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
   public UIOutput getOutput()
   {
      return output;
   }
}
