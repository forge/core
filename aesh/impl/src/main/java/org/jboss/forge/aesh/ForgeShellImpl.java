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
import org.jboss.aesh.cl.exception.ArgumentParserException;
import org.jboss.aesh.cl.exception.CommandLineParserException;
import org.jboss.aesh.cl.exception.OptionParserException;
import org.jboss.aesh.cl.exception.RequiredOptionException;
import org.jboss.aesh.console.Config;
import org.jboss.aesh.console.Console;
import org.jboss.aesh.console.ConsoleCallback;
import org.jboss.aesh.console.ConsoleOutput;
import org.jboss.aesh.console.Prompt;
import org.jboss.aesh.console.settings.Settings;
import org.jboss.aesh.terminal.CharacterType;
import org.jboss.aesh.terminal.Color;
import org.jboss.aesh.terminal.TerminalCharacter;
import org.jboss.forge.aesh.spi.ShellConfiguration;
import org.jboss.forge.aesh.util.CommandLineUtil;
import org.jboss.forge.aesh.util.UICommandDelegate;
import org.jboss.forge.container.Forge;
import org.jboss.forge.container.addons.Addon;
import org.jboss.forge.container.addons.AddonRegistry;
import org.jboss.forge.container.event.PostStartup;
import org.jboss.forge.container.event.PreShutdown;
import org.jboss.forge.container.services.Exported;
import org.jboss.forge.container.services.ExportedInstance;
import org.jboss.forge.ui.UICommand;
import org.jboss.forge.ui.result.NavigationResult;
import org.jboss.forge.ui.result.Result;
import org.jboss.forge.ui.wizard.UIWizard;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
@Singleton
@Exported
public class ForgeShellImpl implements ForgeShell
{
   private static final Logger logger = Logger.getLogger(ForgeShellImpl.class.getName());

   private Console console;
   private Prompt prompt;
    private List<ShellCommand> wizardSteps = new ArrayList<ShellCommand>();

   private List<ShellCommand> commands;

   @Inject
   private Forge forge;

   @Inject
   private Addon self;

   @Inject
   private AddonRegistry registry;

   void observe(@Observes PostStartup startup) throws Exception
   {
      startShell();
   }

   void stop(@Observes PreShutdown shutdown) throws Exception
   {
      if (console != null && console.isRunning())
         console.stop();
   }

   public void addCommand(ShellCommand command)
   {
      commands.add(command);
      console.addCompletion(command);
   }

   private void initShell() throws Exception
   {
      prompt = createPrompt();

      Settings.getInstance().setReadInputrc(false);
      Settings.getInstance().setLogging(true);

      for (ExportedInstance<ShellConfiguration> instance : registry.getExportedInstances(ShellConfiguration.class))
      {
         ShellConfiguration provider = instance.get();
         provider.configure();
      }

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
            addCommand(new ShellCommand(registry, this, command));
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
               addCommand(new ShellCommand(registry, this, instance.get()));
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
         if (new UICommandDelegate(command.get()).getMetadata().getName().equals(name))
            return true;
      }
      return false;
   }

   public void startShell() throws Exception
   {
      initShell();
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
      if (console != null && console.isRunning())
      {
         console.stop();
         console.reset();
      }
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

    private ForgeShell getForgeShell() {
        return this;
    }

   class ForgeConsoleCallback implements ConsoleCallback
   {
      @Override
      public int readConsoleOutput(ConsoleOutput output) throws IOException
      {
         if (output.getBuffer() != null && !output.getBuffer().trim().isEmpty())
         {
            // TODO: should clean this up
            try
            {
               verifyLoadedCommands();
            }
            catch (Exception e)
            {
               logger.log(Level.SEVERE, "Failed to verify commands.", e);
            }
             //we're currently not in a wizard context
             if(wizardSteps.size() == 0)
                 return parseUICommand(output);
             else
                 return parseWizardStep(output);
         }
         return -1;
      }


       /**
        * We're in a wizard and we need to add the steps in a list so they can be executed
        * when we've come to the end of the wizard.
        *
        * @throws IOException
        */
       private int parseWizardStep(ConsoleOutput output) throws IOException
       {
           //get latest command added
           ShellCommand currentCommand = wizardSteps.get(wizardSteps.size() - 1);
           try
           {
               CommandLine cl = currentCommand.parse(output.getBuffer());
               if(cl != null)
               {
                   //populate the inputs to the context
                   CommandLineUtil.populateUIInputs(cl, currentCommand.getContext(), registry);
                   currentCommand.getContext().setConsoleOutput(output);

                   //so now the options are parsed and added to the uicontext of that command
                   //try to get next command in the wizard
                   UIWizard wizard = (UIWizard) wizardSteps.get(0).getCommand();
                   NavigationResult navResult = wizard.next(wizardSteps.get(0).getContext());
                   if(navResult != null)
                   {
                       Object cmd = navResult.getNext();
                       wizardSteps.add(new ShellCommand(registry, getForgeShell(), (UICommand) cmd ));
                   }
               }
               else
                   return executeWizardSteps();

           }
           catch (CommandLineParserException e)
           {
               //parser exception
           }
           catch (Exception e)
           {
               //wizard.next(..) exception
           }

           return 0;
       }

       /**
        * When the wizard is at its final step, go through all the commands and execute them
        *
        * @throws IOException
        */
       private int executeWizardSteps() throws IOException
       {
           for(ShellCommand command : wizardSteps)
           {
               try
               {
                  Result result = command.getCommand().execute(command.getContext());
                   if (result != null &&
                           result.getMessage() != null && result.getMessage().length() > 0)
                       getConsole().pushToStdOut(result.getMessage() + Config.getLineSeparator());
               }
               catch (Exception e)
               {
                  //not sure what we do here, need to think about it
               }
           }
           //empty the list
           wizardSteps.clear();
           return 1;
       }

       private int parseUICommand(ConsoleOutput output) throws IOException
       {
           int result = 1;
           CommandLine cl = null;
           for (ShellCommand command : commands)
            {
               try
               {
                 cl = command.parse(output.getBuffer());
                  logger.info("Parsing: " + output.getBuffer() + ", CommandLine is:" + cl);
                   if (cl != null)
                   {
                       if(command.getCommand() instanceof UIWizard)
                       {
                           wizardSteps.add(command);
                           return parseWizardStep(output);
                       }
                       try
                       {
                           command.run(output, cl);
                           result = 0;
                           break;
                       }
                       catch (Exception e)
                       {
                           logger.log(Level.SEVERE, "Command " + command + " failed to run with: " + output, e);
                           result = 1;
                       }
                   }
               }
               catch (CommandLineParserException iae)
               {
                  if (iae instanceof OptionParserException ||
                           iae instanceof ArgumentParserException ||
                           iae instanceof RequiredOptionException)
                  {
                     console.pushToStdOut(iae.getMessage() + Config.getLineSeparator());
                     logger.info("GOT: " + iae.getMessage() + "\n Parser: " + command.getContext().getParser());
                     result = 1;
                     break;
                  }
                  else
                  {
                     logger.log(Level.INFO, "Command: " + command + ", did not match: " + output.getBuffer() +
                              "\n" + iae.getMessage());
                  }
               }
            }
                // if we didnt find any commands matching
            if (cl == null)
            {
               console.pushToStdOut(output.getBuffer() + ": command not found." + Config.getLineSeparator());
            }

           return result;
       }
   }


}
