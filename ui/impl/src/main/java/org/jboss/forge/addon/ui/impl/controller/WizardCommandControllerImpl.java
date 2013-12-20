/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.impl.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.UIProgressMonitor;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.controller.CommandControllerFactory;
import org.jboss.forge.addon.ui.controller.CommandExecutionListener;
import org.jboss.forge.addon.ui.controller.WizardCommandController;
import org.jboss.forge.addon.ui.impl.context.UIExecutionContextImpl;
import org.jboss.forge.addon.ui.impl.context.UINavigationContextImpl;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.spi.UIRuntime;
import org.jboss.forge.addon.ui.validation.UIValidationMessage;
import org.jboss.forge.addon.ui.wizard.UIWizard;
import org.jboss.forge.furnace.addons.AddonRegistry;

/**
 * 
 * Implementation for the {@link WizardCommandController} interface
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
class WizardCommandControllerImpl extends AbstractCommandController implements WizardCommandController
{
   private Logger logger = Logger.getLogger(getClass().getName());
   /**
    * The execution flow
    */
   private final List<WizardStepEntry> flow = new ArrayList<>();

   /**
    * If there are any subflows, store here
    */
   private final LinkedList<WizardStepEntry> subflow = new LinkedList<>();

   /**
    * The pointer that this flow is on. Starts with 0
    */
   private int flowPointer = 0;

   private final CommandControllerFactory controllerFactory;

   public WizardCommandControllerImpl(UIContext context, AddonRegistry addonRegistry, UIRuntime runtime,
            UIWizard initialCommand, CommandControllerFactory controllerFactory)
   {
      super(addonRegistry, runtime, initialCommand, context);
      this.controllerFactory = controllerFactory;
      flow.add(createEntry(initialCommand, false));
   }

   @Override
   public void initialize() throws Exception
   {
      getCurrentController().initialize();
   }

   @Override
   public boolean isInitialized()
   {
      return getCurrentController().isInitialized();
   }

   @Override
   public Result execute() throws Exception
   {
      Result result = null;

      assertInitialized();
      UIProgressMonitor progressMonitor = runtime.createProgressMonitor(context);
      UIExecutionContextImpl executionContext = new UIExecutionContextImpl(context, progressMonitor);
      Set<CommandExecutionListener> listeners = new LinkedHashSet<>();
      listeners.addAll(context.getListeners());
      for (CommandExecutionListener listener : addonRegistry
               .getServices(CommandExecutionListener.class))
      {
         listeners.add(listener);
      }
      assertValid();
      for (WizardStepEntry entry : flow)
      {
         CommandController controller = entry.controller;
         if (progressMonitor.isCancelled())
         {
            break;
         }
         UICommand command = controller.getCommand();
         try
         {
            for (CommandExecutionListener listener : listeners)
            {
               listener.preCommandExecuted(command, executionContext);
            }

            result = command.execute(executionContext);
            for (CommandExecutionListener listener : listeners)
            {
               listener.postCommandExecuted(command, executionContext, result);
            }
         }
         catch (Exception e)
         {
            for (CommandExecutionListener listener : listeners)
            {
               listener.postCommandFailure(command, executionContext, e);
            }

            throw e;
         }
      }
      return result;
   }

   @Override
   public List<UIValidationMessage> validate()
   {
      return getCurrentController().validate();
   }

   @Override
   public boolean isValid()
   {
      return getCurrentController().isValid();
   }

   @Override
   public CommandController setValueFor(String inputName, Object value) throws IllegalArgumentException
   {
      getCurrentController().setValueFor(inputName, value);
      return this;
   }

   @Override
   public Object getValueFor(String inputName) throws IllegalArgumentException
   {
      return getCurrentController().getValueFor(inputName);
   }

   @Override
   public Map<String, InputComponent<?, ?>> getInputs()
   {
      return getCurrentController().getInputs();
   }

   @Override
   public UICommandMetadata getMetadata()
   {
      return getCurrentController().getMetadata();
   }

   @Override
   public UICommandMetadata getInitialMetadata()
   {
      return flow.get(0).controller.getMetadata();
   }

   @Override
   public boolean isEnabled()
   {
      return getCurrentController().isEnabled();
   }

   @Override
   public UICommand getCommand()
   {
      return getCurrentController().getCommand();
   }

   @Override
   public void close() throws Exception
   {
      context.close();
   }

   @Override
   public boolean canMoveToNextStep()
   {
      Class<? extends UICommand>[] next = getNextFrom(getCurrentController().getCommand());
      return (getNextEntry() != null || (next != null || !subflow.isEmpty()));
   }

   @Override
   public boolean canMoveToPreviousStep()
   {
      return flowPointer > 0;
   }

   @Override
   public boolean canExecute()
   {
      for (WizardStepEntry entry : flow)
      {
         if (!entry.controller.canExecute())
         {
            return false;
         }
      }

      // Checking if there is any next page left
      Class<? extends UICommand>[] next = getNextFrom(flow.get(flow.size() - 1).controller.getCommand());
      if (next != null || !subflow.isEmpty())
      {
         return false;
      }
      return true;
   }

   @Override
   public WizardCommandController next() throws Exception
   {
      assertInitialized();
      assertValid();

      WizardStepEntry currentEntry = getCurrentEntry();
      WizardStepEntry nextEntry = getNextEntry();
      Class<? extends UICommand>[] result = getNextFrom(currentEntry.controller.getCommand());
      if (nextEntry == null)
      {
         addNextFlowStep(result);
      }
      else
      {
         // There is already a next page, did the object returned from UICommand.next() changed ?
         if (!Arrays.equals(currentEntry.next, result))
         {
            // Update current entry
            currentEntry.next = result;
            cleanSubsequentStalePages();
            addNextFlowStep(result);
         }
         else
         {
            // FORGE-1372- Test if the inputs changed.
            final UICommand command;
            if (result == null)
            {
               if (subflow.isEmpty())
               {
                  command = null;
               }
               else
               {
                  command = createCommand(subflow.peek().controller.getCommand().getClass());
               }
            }
            else
            {
               command = createCommand(result[0]);
            }
            if (command != null)
            {
               CommandController ctrl = controllerFactory.createController(context, command, runtime);
               ctrl.initialize();
               Set<String> currentInputsKeySet = nextEntry.controller.getInputs().keySet();
               Set<String> keySet = ctrl.getInputs().keySet();
               if (!(currentInputsKeySet.containsAll(keySet) && keySet.containsAll(currentInputsKeySet)))
               {
                  cleanSubsequentStalePages();
                  addNextFlowStep(result);
               }
            }
         }
      }
      flowPointer++;
      return this;
   }

   /**
    * 
    */
   private void cleanSubsequentStalePages()
   {
      // Remove subsequent pages and push the subflows back to the stack
      Iterator<WizardStepEntry> it = flow.listIterator(flowPointer + 1);
      int subflowIdx = 0;
      while (it.hasNext())
      {
         WizardStepEntry entry = it.next();
         if (entry.subflowHead && !subflow.contains(entry))
         {
            subflow.add(subflowIdx++, entry);
         }
         it.remove();
      }
   }

   /**
    * @param result
    */
   private void addNextFlowStep(Class<? extends UICommand>[] result)
   {
      final WizardStepEntry next;
      if (result == null)
      {
         if (subflow.isEmpty())
         {
            throw new IllegalStateException("No next step found");
         }
         else
         {
            next = subflow.pop();
         }
      }
      else
      {
         next = createEntry(result[0], false);
         for (int i = 1; i < result.length; i++)
         {
            // Save this subflow for later
            WizardStepEntry subflowEntry = createEntry(result[i], true);
            if (!subflow.contains(subflowEntry))
            {
               subflow.add(subflowEntry);
            }
         }
      }
      flow.add(next);
   }

   @Override
   public WizardCommandController previous() throws IllegalStateException
   {
      assertInitialized();
      if (!canMoveToPreviousStep())
      {
         throw new IllegalStateException("No previous step found");
      }
      flowPointer--;
      return this;
   }

   private WizardStepEntry getCurrentEntry()
   {
      return flow.get(flowPointer);
   }

   private WizardStepEntry getNextEntry()
   {
      int nextIdx = flowPointer + 1;
      return (nextIdx < flow.size()) ? flow.get(nextIdx) : null;
   }

   private CommandController getCurrentController()
   {
      return getCurrentEntry().controller;
   }

   private WizardStepEntry createEntry(Class<? extends UICommand> commandClass, boolean subflowHead)
   {
      UICommand command = createCommand(commandClass);
      return createEntry(command, subflowHead);
   }

   private UICommand createCommand(Class<? extends UICommand> commandClass)
   {
      UICommand command = addonRegistry.getServices(commandClass).get();
      return command;
   }

   private WizardStepEntry createEntry(UICommand command, boolean subflowHead)
   {
      CommandController controller = controllerFactory.createSingleController(context, command, runtime);
      Class<? extends UICommand>[] next = getNextFrom(command);
      return new WizardStepEntry(controller, next, subflowHead);
   }

   private Class<? extends UICommand>[] getNextFrom(UICommand command)
   {
      Class<? extends UICommand>[] result = null;
      if (command instanceof UIWizard)
      {
         NavigationResult next;
         try
         {
            next = ((UIWizard) command).next(new UINavigationContextImpl(context));
         }
         catch (Exception e)
         {
            logger.log(Level.SEVERE, "Cannot fetch the next steps from " + command, e);
            next = null;
         }
         if (next != null)
         {
            result = next.getNext();
         }
      }
      return result;
   }

   private static class WizardStepEntry
   {
      final CommandController controller;
      Class<? extends UICommand>[] next;
      // If this entry starts a subflow
      final boolean subflowHead;

      public WizardStepEntry(CommandController controller, Class<? extends UICommand>[] next, boolean subflowHead)
      {
         this.controller = controller;
         this.next = next;
         this.subflowHead = subflowHead;
      }

      @Override
      public int hashCode()
      {
         final int prime = 31;
         int result = 1;
         result = prime * result + ((controller == null) ? 0 : controller.hashCode());
         result = prime * result + Arrays.hashCode(next);
         return result;
      }

      @Override
      public boolean equals(Object obj)
      {
         if (this == obj)
            return true;
         if (obj == null)
            return false;
         if (getClass() != obj.getClass())
            return false;
         WizardStepEntry other = (WizardStepEntry) obj;
         if (controller == null)
         {
            if (other.controller != null)
               return false;
         }
         else if (!controller.equals(other.controller))
            return false;
         if (!Arrays.equals(next, other.next))
            return false;
         return true;
      }
   }
}