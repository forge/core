/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
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
import org.jboss.aesh.console.settings.SettingsBuilder;
import org.jboss.aesh.terminal.CharacterType;
import org.jboss.aesh.terminal.Color;
import org.jboss.aesh.terminal.TerminalCharacter;
import org.jboss.forge.addon.shell.spi.AeshSettingsProvider;
import org.jboss.forge.addon.shell.spi.CommandExecutionListener;
import org.jboss.forge.addon.shell.util.CommandLineUtil;
import org.jboss.forge.addon.shell.util.UICommandDelegate;
import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.wizard.UIWizard;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.event.PostStartup;
import org.jboss.forge.furnace.event.PreShutdown;
import org.jboss.forge.furnace.services.Exported;
import org.jboss.forge.furnace.services.Imported;
import org.jboss.forge.furnace.spi.ListenerRegistration;
import org.jboss.forge.furnace.util.Assert;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
@Singleton
@Exported
public class ForgeShellImpl implements ForgeShell
{
   private static final Logger logger = Logger.getLogger(ForgeShellImpl.class.getName());

   private Console console;
   private Settings settings;
   private Prompt prompt;
   private List<ShellCommand> wizardSteps = new ArrayList<ShellCommand>();
   
   private InputStream inputStream;
   private OutputStream stdOut, stdErr;

   private List<ShellCommand> commands;

   @Inject
   private AddonRegistry registry;

   private List<CommandExecutionListener> commandListeners = new CopyOnWriteArrayList<CommandExecutionListener>();

   void observe(@Observes PostStartup startup) throws Exception
   {
      if (!Boolean.getBoolean("forge.compatibility.IDE")) {
         startShell();
      }
   }
   
   @Override
   public void setInputStream(InputStream is) {
      inputStream = is;
   }
   
   @Override
   public void setStdOut(OutputStream os) {
      stdOut = os;
   }
   
   @Override
   public void setStdErr(OutputStream os) {
      stdErr = os;
   }

   @Override
   public void startShell() throws Exception
   {
      if (console != null && console.isRunning()) return;
      initShell();
      console.start();
   }

   void stop(@Observes PreShutdown shutdown) throws Exception
   {
      stopShell();
   }

   @Override
   public void stopShell() throws IOException
   {
      if (console != null && console.isRunning())
      {
         console.stop();
      }
   }

   public void addCommand(ShellCommand command)
   {
      commands.add(command);
      console.addCompletion(command);
   }

   private void initShell() throws Exception
   {
      prompt = createPrompt();

      Imported<AeshSettingsProvider> instances = registry.getServices(AeshSettingsProvider.class);
      for (AeshSettingsProvider provider : instances)
      {
         settings = provider.buildAeshSettings();
         instances.release(provider);
      }
      
      if (settings == null) {
         SettingsBuilder sb = new SettingsBuilder()
            .readInputrc(false)
            .logging(true);
         if (inputStream != null) {
            sb.inputStream(inputStream);
         }
         if (stdOut != null) {
            sb.outputStream(stdOut);
         }
         if (stdErr != null) {
            sb.outputStreamError(stdErr);
         }  
         settings = sb.create();
      }

      commands = new ArrayList<ShellCommand>();
      
      console = new Console(settings);
      console.setPrompt(prompt);
      console.setConsoleCallback(new ForgeConsoleCallback());

      refreshAvailableCommands();
   }

   private void refreshAvailableCommands()
   {
      Imported<UICommand> instances = registry.getServices(UICommand.class);

      Set<UICommand> loaded = new HashSet<UICommand>();
      for (UICommand command : instances)
      {
         loaded.add(command);
         if (!isCommandLoaded(command))
         {
            try
            {
               addCommand(new ShellCommand(registry, this, command));
            }
            catch (Exception e)
            {
               logger.log(Level.SEVERE, "Failed to load command [" + command + "]");
            }
         }
      }

      Iterator<ShellCommand> iterable = commands.iterator();
      Set<ShellCommand> toRemove = new HashSet<ShellCommand>();
      while (iterable.hasNext())
      {
         ShellCommand next = iterable.next();
         if (!isCommandAvailable(loaded, next.getCommand().getMetadata().getName()))
         {
            toRemove.add(next);
         }
      }
      commands.removeAll(toRemove);
   }

   private boolean isCommandLoaded(UICommand uiCommand)
   {
      for (ShellCommand command : commands)
      {
         if (command.getCommand().getMetadata().getName()
                  .equals(new UICommandDelegate(uiCommand).getMetadata().getName()))
            return true;
      }
      return false;
   }

   private boolean isCommandAvailable(Set<UICommand> availableCommands, String name)
   {
      for (UICommand command : availableCommands)
      {
         if (new UICommandDelegate(command).getMetadata().getName().equals(name))
            return true;
      }
      return false;
   }

   public AddonRegistry getRegistry()
   {
      return registry;
   }

   @Override
   public String getPrompt()
   {
      return prompt.getPromptAsString();
   }

   @Override
   public Console getConsole()
   {
      return console;
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

   private ForgeShell getForgeShell()
   {
      return this;
   }

   class ForgeConsoleCallback implements ConsoleCallback
   {
      @Override
      public int readConsoleOutput(ConsoleOutput output) throws IOException
      {
         refreshAvailableCommands();
         if (output.getBuffer() != null && !output.getBuffer().trim().isEmpty())
         {
            // we're currently not in a wizard context
            if (wizardSteps.size() == 0)
               return parseUICommand(output);
            else
               return parseWizardStep(output);
         }
         return -1;
      }

      /**
       * We're in a wizard and we need to add the steps in a list so they can be executed when we've come to the end of
       * the wizard.
       * 
       * @throws IOException
       */
      private int parseWizardStep(ConsoleOutput output) throws IOException
      {
         // get latest command added
         ShellCommand currentCommand = wizardSteps.get(wizardSteps.size() - 1);
         try
         {
            CommandLine cl = currentCommand.parse(output.getBuffer());
            if (cl != null)
            {
               // populate the inputs to the context
               CommandLineUtil.populateUIInputs(cl, currentCommand.getContext(), registry);
               currentCommand.getContext().setConsoleOutput(output);

               // so now the options are parsed and added to the uicontext of that command
               // try to get next command in the wizard
               UIWizard wizard = (UIWizard) wizardSteps.get(0).getCommand();
               NavigationResult navResult = wizard.next(wizardSteps.get(0).getContext());
               // we have another step, lets add it to the list
               // this will be processed next
               if (navResult != null)
               {
                  Object cmd = navResult.getNext();
                  wizardSteps.add(new ShellCommand(registry, getForgeShell(), (UICommand) cmd));
               }
               // we have come to the final step, execute the wizard
               else
                  return executeWizardSteps(output);
            }

         }
         catch (CommandLineParserException e)
         {
            // parser exception (we must add some logic here to give the user more info on
            // what input was wrong to prevent loosing all the steps in the wizard.
         }
         catch (Exception e)
         {
            // wizard.next(..) exception
         }

         return 0;
      }

      /**
       * When the wizard is at its final step, go through all the commands and execute them
       * 
       * @throws IOException
       */
      private int executeWizardSteps(ConsoleOutput output) throws IOException
      {
         for (ShellCommand command : wizardSteps)
         {
            Result result = null;
            try
            {
               invokePreCommandExecutedListeners(command.getCommand(), command.getContext());
               result = command.getCommand().execute(command.getContext());
               if (result != null &&
                        result.getMessage() != null && result.getMessage().length() > 0)
                  getConsole().pushToStdOut(result.getMessage() + Config.getLineSeparator());
            }
            catch (Exception e)
            {
               result = Results.fail("Command " + command + " failed to run with: " + output, e);
               result = Results.fail("Failed to execute:" + getConsole().getHistory().getCurrent(), e);
            }
            finally
            {
               invokePostCommandExecutedListeners(command.getCommand(), command.getContext(), result);
            }
         }
         // empty the list
         wizardSteps.clear();
         return 1;
      }

      private void invokePreCommandExecutedListeners(UICommand command, ShellContext context)
      {
         for (CommandExecutionListener listener : commandListeners)
         {
            listener.preCommandExecuted(command, context);
         }

         Imported<CommandExecutionListener> instances = registry
                  .getServices(CommandExecutionListener.class);
         for (CommandExecutionListener listener : instances)
         {
            if (listener != null)
               listener.preCommandExecuted(command, context);
            instances.release(listener);
         }
      }

      private void invokePostCommandExecutedListeners(UICommand command, ShellContext context, Result result)
      {
         for (CommandExecutionListener listener : commandListeners)
         {
            listener.postCommandExecuted(command, context, result);
         }

         Imported<CommandExecutionListener> instances = registry
                  .getServices(CommandExecutionListener.class);
         for (CommandExecutionListener listener : instances)
         {
            if (listener != null)
               listener.postCommandExecuted(command, context, result);
            instances.release(listener);
         }
      }

      private int parseUICommand(ConsoleOutput output) throws IOException
      {
         CommandLine cl = null;
         for (ShellCommand command : commands)
         {
            try
            {
               cl = command.parse(output.getBuffer());
               logger.info("Parsing: " + output.getBuffer() + ", CommandLine is:" + cl);
               if (cl != null)
               {
                  if (command.getCommand() instanceof UIWizard)
                  {
                     wizardSteps.add(command);
                     return parseWizardStep(output);
                  }

                  Result result = null;
                  try
                  {
                     invokePreCommandExecutedListeners(command.getCommand(), command.getContext());
                     result = command.run(output, cl);
                     break;
                  }
                  catch (Exception e)
                  {
                     result = Results.fail("Command " + command + " failed to run with: " + output, e);
                     logger.log(Level.SEVERE, "Command " + command + " failed to run with: " + output, e);
                  }
                  finally
                  {
                     invokePostCommandExecutedListeners(command.getCommand(), command.getContext(), result);
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

         return 0;
      }
   }

   @Override
   public ListenerRegistration<CommandExecutionListener> addCommandExecutionListener(
            final CommandExecutionListener listener)
   {
      Assert.notNull(listener, "Listener must not be null.");
      commandListeners.add(listener);

      return new ListenerRegistration<CommandExecutionListener>()
      {
         @Override
         public CommandExecutionListener removeListener()
         {
            commandListeners.remove(listener);
            return listener;
         }
      };
   }

}
