/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PreDestroy;
import javax.enterprise.inject.Vetoed;

import org.jboss.aesh.console.AeshConsole;
import org.jboss.aesh.console.AeshConsoleBuilder;
import org.jboss.aesh.console.AeshConsoleImpl;
import org.jboss.aesh.console.Console;
import org.jboss.aesh.console.Prompt;
import org.jboss.aesh.console.command.CommandNotFoundException;
import org.jboss.aesh.console.command.container.CommandContainer;
import org.jboss.aesh.console.command.invocation.AeshCommandInvocation;
import org.jboss.aesh.console.export.ExportManager;
import org.jboss.aesh.console.helper.InterruptHook;
import org.jboss.aesh.console.operator.ControlOperator;
import org.jboss.aesh.console.settings.Settings;
import org.jboss.aesh.console.settings.SettingsBuilder;
import org.jboss.aesh.edit.actions.Action;
import org.jboss.aesh.terminal.CharacterType;
import org.jboss.aesh.terminal.Color;
import org.jboss.aesh.terminal.POSIXTerminal;
import org.jboss.aesh.terminal.TerminalCharacter;
import org.jboss.aesh.terminal.TerminalColor;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.shell.aesh.ForgeCommandNotFoundHandler;
import org.jboss.forge.addon.shell.aesh.ForgeCommandRegistry;
import org.jboss.forge.addon.shell.ui.DidYouMeanCommandNotFoundListener;
import org.jboss.forge.addon.shell.ui.ShellContext;
import org.jboss.forge.addon.shell.ui.ShellContextImpl;
import org.jboss.forge.addon.shell.ui.ShellUIOutputImpl;
import org.jboss.forge.addon.shell.ui.ShellUIProgressMonitor;
import org.jboss.forge.addon.shell.ui.ShellUIPromptImpl;
import org.jboss.forge.addon.ui.DefaultUIDesktop;
import org.jboss.forge.addon.ui.UIDesktop;
import org.jboss.forge.addon.ui.UIRuntime;
import org.jboss.forge.addon.ui.command.CommandExecutionListener;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIContextListener;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.output.UIOutput;
import org.jboss.forge.addon.ui.progress.UIProgressMonitor;
import org.jboss.forge.addon.ui.util.Selections;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.services.Imported;
import org.jboss.forge.furnace.spi.ListenerRegistration;
import org.jboss.forge.furnace.util.Assert;
import org.jboss.forge.furnace.util.OperatingSystemUtils;

/**
 * Implementation of the {@link Shell} interface.
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Vetoed
public class ShellImpl implements Shell, UIRuntime
{
   private Resource<?> currentResource;

   private final AddonRegistry addonRegistry;
   private final AeshConsole console;
   private final UIOutput output;
   private UIDesktop desktop;
   private boolean embedded;
   private String name = "Shell";
   private final List<CommandExecutionListener> executionListeners = new LinkedList<>();
   private final List<CommandNotFoundListener> commandNotFoundListeners = new LinkedList<>();

   private final static Logger log = Logger.getLogger(ShellImpl.class.getName());

   public ShellImpl(Furnace furnace, Resource<?> initialResource, Settings settings, AddonRegistry addonRegistry)
   {
      this.addonRegistry = addonRegistry;

      // Set the paths for the Aesh history, alias and export files.
      File forgeHome = OperatingSystemUtils.getUserForgeDir();
      File history = new File(forgeHome, "history");
      File alias = new File(forgeHome, "alias");
      File export = new File(forgeHome, "export");
      final ForgeCommandRegistry registry = new ForgeCommandRegistry(furnace, this, addonRegistry);
      // Register DidYouMeanListener
      commandNotFoundListeners.add(new DidYouMeanCommandNotFoundListener(registry));
      SettingsBuilder newSettings = new SettingsBuilder(settings)
               .historyFile(history)
               .aliasFile(alias)
               .exportFile(export)
               .enableExport(true)
               .setExportUsesSystemEnvironment(true)
               .interruptHook(new ForgeInterruptHook(registry));
      // If system property is set, force POSIXTerminal
      if (Boolean.getBoolean("org.jboss.forge.addon.shell.forcePOSIXTerminal"))
      {
         newSettings.terminal(new POSIXTerminal());
      }
      // This conflicts with the provided Man in ForgeCommandRegistry
      newSettings.enableMan(false);
      this.console = new AeshConsoleBuilder()
               .prompt(createPrompt(initialResource))
               .settings(newSettings.create())
               .commandRegistry(registry)
               .commandNotFoundHandler(new ForgeCommandNotFoundHandler(this, commandNotFoundListeners))
               .create();
      this.output = new ShellUIOutputImpl(console);
      setCurrentResource(initialResource);
      this.console.start();
   }

   private void updatePrompt()
   {
      console.setPrompt(createPrompt(getCurrentResource()));
   }

   /**
    * Creates an initial prompt
    */
   private static Prompt createPrompt(Resource<?> currentResource)
   {
      // [ currentdir]$
      if (OperatingSystemUtils.isWindows())
      {
         List<TerminalCharacter> prompt = new LinkedList<>();
         prompt.add(new TerminalCharacter('['));
         for (char c : currentResource.getName().toCharArray())
         {
            prompt.add(new TerminalCharacter(c));
         }
         prompt.add(new TerminalCharacter(']'));
         prompt.add(new TerminalCharacter('$'));
         prompt.add(new TerminalCharacter(' '));
         return new Prompt(prompt);
      }
      else
      {
         List<TerminalCharacter> prompt = new LinkedList<>();
         prompt.add(new TerminalCharacter('[', new TerminalColor(Color.BLUE, Color.DEFAULT),
                  CharacterType.BOLD));
         for (char c : currentResource.getName().toCharArray())
         {
            prompt.add(new TerminalCharacter(c));
         }
         prompt.add(new TerminalCharacter(']', new TerminalColor(Color.BLUE, Color.DEFAULT),
                  CharacterType.BOLD));
         prompt.add(new TerminalCharacter('$'));
         prompt.add(new TerminalCharacter(' '));
         return new Prompt(prompt);
      }
   }

   @PreDestroy
   @Override
   public void close()
   {
      this.executionListeners.clear();
      this.commandNotFoundListeners.clear();
      this.console.stop();
   }

   @Override
   public Resource<?> getCurrentResource()
   {
      return currentResource;
   }

   @Override
   public void setCurrentResource(final Resource<?> resource)
   {
      Assert.notNull(resource, "Current resource should not be null");
      this.currentResource = resource;

      Resource<?> temp = resource;
      while (!(temp instanceof DirectoryResource) && temp != null)
      {
         temp = temp.getParent();
      }
      if (temp instanceof DirectoryResource)
      {
         // Workaround to prevent "Current working directory must be a directory" exceptions when running in a
         // transaction
         File dir = ((DirectoryResource) temp).getUnderlyingResourceObject();
         if (dir.exists())
         {
            console.getAeshContext().setCurrentWorkingDirectory(new org.jboss.aesh.io.FileResource(dir));
         }
      }
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

   public ShellContextImpl createUIContext()
   {
      Imported<UIContextListener> listeners = addonRegistry.getServices(UIContextListener.class);
      UISelection<?> initialSelection = Selections.from(currentResource);
      ShellContextImpl shellContextImpl = new ShellContextImpl(this, initialSelection, listeners);
      for (CommandExecutionListener listener : executionListeners)
      {
         shellContextImpl.addCommandExecutionListener(listener);
      }
      ExportManager exportManager = null;
      if (console != null)
         exportManager = console.getExportManager();
      if (exportManager != null)
      {
         Map<Object, Object> attributeMap = shellContextImpl.getAttributeMap();
         for (String variableName : exportManager.getAllNames())
         {
            String variableValue = exportManager.getValue(variableName);
            attributeMap.put(variableName, variableValue);
         }
      }
      return shellContextImpl;
   }

   @Override
   public ListenerRegistration<CommandExecutionListener> addCommandExecutionListener(
            final CommandExecutionListener listener)
   {
      executionListeners.add(listener);
      return new ListenerRegistration<CommandExecutionListener>()
      {
         @Override
         public CommandExecutionListener removeListener()
         {
            executionListeners.remove(listener);
            return listener;
         }
      };
   }

   @Override
   public ListenerRegistration<CommandNotFoundListener> addCommandNotFoundListener(
            final CommandNotFoundListener listener)
   {
      commandNotFoundListeners.add(listener);
      return new ListenerRegistration<CommandNotFoundListener>()
      {
         @Override
         public CommandNotFoundListener removeListener()
         {
            commandNotFoundListeners.remove(listener);
            return listener;
         }
      };
   }

   @Override
   public UIProgressMonitor createProgressMonitor(UIContext context)
   {
      return new ShellUIProgressMonitor(console.getShell().out());
   }

   @Override
   public ShellUIPromptImpl createPrompt(UIContext context)
   {
      ShellContext shellContext = (ShellContext) context;
      ConverterFactory converterFactory = addonRegistry.getServices(ConverterFactory.class).get();
      return new ShellUIPromptImpl(shellContext, converterFactory);
   }

   /**
    * Handles interrupts in AeshConsole
    */
   private class ForgeInterruptHook implements InterruptHook
   {

      private final ForgeCommandRegistry registry;

      ForgeInterruptHook(ForgeCommandRegistry registry)
      {
         this.registry = registry;
      }

      @SuppressWarnings("unchecked")
      @Override
      public void handleInterrupt(Console console, Action action)
      {
         if (action == Action.INTERRUPT)
         {
            console.getShell().out().println("^C");
            console.clearBufferAndDisplayPrompt();
         }
         else if (action == Action.IGNOREEOF)
         {
            console.getShell().out().println("Use \"exit\" to leave the shell.");
            console.clearBufferAndDisplayPrompt();
         }
         else
         {
            try
            {
               CommandContainer<?> exitCommand = registry.getCommand("exit", "");
               // print a new line so we exit nicely
               console.getShell().out().println();
               exitCommand.getParser().getCommand().execute(
                        new AeshCommandInvocation((AeshConsoleImpl) ShellImpl.this.console, ControlOperator.NONE, 1,
                                 null));
            }
            catch (InterruptedException | CommandNotFoundException | IOException e)
            {
               log.log(Level.WARNING, "Error while trying to run exit", e);
            }
         }
      }
   }

   public void setDesktop(UIDesktop desktop)
   {
      this.desktop = desktop;
   }

   @Override
   public UIDesktop getDesktop()
   {
      if (desktop == null)
         desktop = new DefaultUIDesktop();
      return desktop;
   }

   @Override
   public String getName()
   {
      return name;
   }

   @Override
   public boolean isEmbedded()
   {
      return embedded;
   }

   /**
    * @param name the name to set
    */
   public void setName(String name)
   {
      this.name = name;
   }

   /**
    * @param embedded the embedded to set
    */
   public void setEmbedded(boolean embedded)
   {
      this.embedded = embedded;
   }
}
