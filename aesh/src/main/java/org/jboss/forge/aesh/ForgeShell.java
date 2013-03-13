/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.aesh;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.aesh.cl.CommandLine;
import org.jboss.aesh.console.Config;
import org.jboss.aesh.console.Console;
import org.jboss.aesh.console.ConsoleCallback;
import org.jboss.aesh.console.ConsoleOutput;
import org.jboss.aesh.console.Prompt;
import org.jboss.aesh.console.settings.Settings;
import org.jboss.aesh.terminal.CharacterType;
import org.jboss.aesh.terminal.Color;
import org.jboss.aesh.terminal.TerminalCharacter;
import org.jboss.forge.container.ContainerControl;
import org.jboss.forge.container.addons.AddonRegistry;
import org.jboss.forge.container.event.Perform;
import org.jboss.forge.container.services.Exported;
import org.jboss.forge.container.services.ExportedInstance;
import org.jboss.forge.ui.UICommand;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
@Singleton
@Exported
public class ForgeShell
{
   private static final Logger logger = Logger.getLogger(ForgeShell.class.getName());

   private Console console;
   private Prompt prompt;

   private List<ShellCommand> commands;

   @Inject
   private ContainerControl containerControl;

   @Inject
   private AddonRegistry registry;

   public void observe(@Observes Perform startup) throws Exception
   {
      initShell();
      startShell();
   }

   public void addCommand(ShellCommand command)
   {
      commands.add(command);
      console.addCompletion(command);
   }

   public void initShell() throws Exception
   {
      prompt = createPrompt();

      for (ExportedInstance<ShellStreamProvider> provider : registry.getExportedInstances(ShellStreamProvider.class))
      {
         System.out.println("Loaded: ShellStreamProvider - " + provider.get());
         // Configure the stream here. This will only execute if there is a addon deployed BEFORE aesh, which is the
         // case in tests. We control that order there (or can fix if it is not working.)

         // in the test @Inject the ShellStreamProvider and if there is one deployed, it will work. Otherwise there
         // will be problems when you call .get();
      }

      Settings.getInstance().setReadInputrc(false);
      Settings.getInstance().setLogging(true);

      commands = new ArrayList<ShellCommand>();
      console = Console.getInstance();
      console.setPrompt(prompt);
      console.setConsoleCallback(new ForgeConsoleCallback());

      // internal commands
      // addCommand(new ShellCommand(listServicesCommand));
      for (ExportedInstance<UICommand> instance : registry.getExportedInstances(UICommand.class))
      {
         UICommand command = instance.get();
         try
         {
            addCommand(new ShellCommand(command, this));
         }
         catch (Exception e)
         {
            logger.log(Level.SEVERE, "Could not load UICommand [" + command.getMetadata().getName() + "]", e);
         }
      }
   }

   // not very optimal atm.
   private void verifyLoadedCommands() throws Exception
   {
      // only bother to check whats been added/removed if it doesnt
      // have the same size
      if (registry.getExportedInstances(UICommand.class).size() != commands.size())
      {
         for (ExportedInstance<UICommand> instance : registry.getExportedInstances(UICommand.class))
         {
            if (!doCommandExist(instance.get().getMetadata().getName()))
            {
               addCommand(new ShellCommand(instance.get(), this));
            }
         }
         Iterator<ShellCommand> iterable = commands.iterator();
         while (iterable.hasNext())
         {
            if (!isCommandFound(iterable.next().getCommand().getMetadata().getName(),
                     registry.getExportedInstances(UICommand.class)))
            {
               iterable.remove();
            }
         }
      }
   }

   private boolean doCommandExist(String name)
   {
      for (ShellCommand command : commands)
      {
         if (command.getCommand().getMetadata().getName().equals(name))
            return true;
      }
      return false;
   }

   private boolean isCommandFound(String name, Set<ExportedInstance<UICommand>> uiCommands)
   {
      for (ExportedInstance<UICommand> command : uiCommands)
      {
         if (command.get().getMetadata().getName().equals(name))
            return true;
      }
      return false;
   }

   public void startShell() throws Exception
   {
      console.start();
   }

   public AddonRegistry getRegistry()
   {
      return registry;
   }

   public String getPrompt()
   {
      return prompt.getPromptAsString();
   }

   public Console getConsole()
   {
      return console;
   }

   public void stopShell() throws IOException
   {
      if (console != null)
         console.stop();
      containerControl.stop();
   }

   private Prompt createPrompt()
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

    class ForgeConsoleCallback implements ConsoleCallback {
        @Override
        public int readConsoleOutput(ConsoleOutput output) throws IOException {
            CommandLine cl = null;
            if (output.getBuffer() != null && !output.getBuffer().trim().isEmpty())
            {
                //TODO: should clean this up
                try {
                    verifyLoadedCommands();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                for (ShellCommand command : commands)
                {
                    try
                    {
                        cl = command.parse(output.getBuffer());
                        if (cl != null)
                        {
                            // need some way of deciding if the command is standalone
                            if (command.getContext().isStandalone())
                            {
                                // console.

                            }
                            else
                            {
                                try {
                                    command.run(output, cl);
                                    break;
                                }
                                catch (Exception e) {
                                    logger.log(Level.SEVERE, "Command "+command+" failed to run with: "+output);
                                }
                            }
                        }
                    }
                    catch (IllegalArgumentException iae)
                    {
                        // System.out.println("Command: " + command + ", did not match: " + output.getBuffer());
                        // ignored for now
                    }
                }
                // if we didnt find any commands matching
                if (cl == null)
                {
                    console.pushToStdOut(output.getBuffer() + ": command not found."
                            + Config.getLineSeparator());
                }
            }
            return 0;
        }
    }
}
