/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.impl.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.forge.addon.ui.UIRuntime;
import org.jboss.forge.addon.ui.command.CommandExecutionListener;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.controller.WizardCommandController;
import org.jboss.forge.addon.ui.impl.context.DelegatingUIContext;
import org.jboss.forge.addon.ui.impl.context.UIBuilderImpl;
import org.jboss.forge.addon.ui.impl.context.UIExecutionContextImpl;
import org.jboss.forge.addon.ui.impl.context.UINavigationContextImpl;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.InputComponentFactory;
import org.jboss.forge.addon.ui.input.UIPrompt;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.output.UIMessage;
import org.jboss.forge.addon.ui.progress.UIProgressMonitor;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.NavigationResultEntry;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.result.navigation.NavigationResultTransformer;
import org.jboss.forge.addon.ui.wizard.UIWizard;
import org.jboss.forge.addon.ui.wizard.WizardExecutionListener;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.util.Assert;
import org.jboss.forge.furnace.util.Lists;

/**
 * Implementation for the {@link WizardCommandController} interface
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
class WizardCommandControllerImpl extends AbstractCommandController implements WizardCommandController
{
   private final Logger logger = Logger.getLogger(getClass().getName());
   /**
    * The execution flow
    */
   private final List<WizardStepEntry> flow = new ArrayList<>();

   /**
    * If there are any subflows, store here
    */
   private final LinkedList<WizardStepEntry> subflow = new LinkedList<>();

   private final Set<Integer> usedSubflows = new HashSet<>();

   /**
    * The pointer that this flow is on. Starts with 0
    */
   private int flowPointer = 0;

   private final CommandControllerFactoryImpl controllerFactory;
   private final InputComponentFactory inputComponentFactory;

   public WizardCommandControllerImpl(UIContext context, AddonRegistry addonRegistry, UIRuntime runtime,
            UIWizard initialCommand, CommandControllerFactoryImpl controllerFactory)
   {
      super(addonRegistry, runtime, initialCommand, context);
      this.inputComponentFactory = addonRegistry.getServices(InputComponentFactory.class).get();
      this.controllerFactory = controllerFactory;
      flow.add(createEntry(initialCommand, false));
   }

   /**
    * Refreshes the current flow so it's possible to eagerly fetch all the steps
    */
   private synchronized void refreshFlow()
   {
      try
      {
         initialize();
      }
      catch (Exception e)
      {
         throw new IllegalStateException("Error while initializing wizard", e);
      }
      int currentFlowPointer = this.flowPointer;
      try
      {
         this.flowPointer = 0;
         while (canMoveToNextStep())
         {
            try
            {
               next().initialize();
            }
            catch (Exception e)
            {
               throw new IllegalStateException("Error while moving to the next wizard step", e);
            }
         }
         cleanSubsequentStalePages();
      }
      finally
      {
         this.flowPointer = currentFlowPointer;
      }
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
      assertInitialized();
      assertValid();
      Assert.isTrue(canExecute(), "Controller cannot be executed");
      UIProgressMonitor progressMonitor = runtime.createProgressMonitor(context);
      UIPrompt prompt = runtime.createPrompt(context);
      UIExecutionContextImpl executionContext = new UIExecutionContextImpl(context, progressMonitor, prompt);
      Set<CommandExecutionListener> listeners = new LinkedHashSet<>();
      listeners.addAll(context.getListeners());
      for (CommandExecutionListener listener : addonRegistry
               .getServices(CommandExecutionListener.class))
      {
         listeners.add(listener);
      }
      List<Result> results = new LinkedList<>();
      try
      {
         firePreWizardExecuted(executionContext, listeners);
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
               firePreCommandExecuted(executionContext, listeners, command);
               Result currentResult = command.execute(executionContext);
               results.add(currentResult);
               firePostCommandExecuted(executionContext, listeners, command, currentResult);
            }
            catch (Exception e)
            {
               firePostCommandFailure(executionContext, listeners, command, e);
               throw e;
            }
         }
      }
      catch (Exception e)
      {
         firePostWizardFailure(executionContext, listeners, e);
         throw e;
      }
      Result result = (results.size() == 1) ? results.get(0) : Results.aggregate(results);
      firePostWizardExecuted(executionContext, listeners, result);
      return result;
   }

   protected void firePreWizardExecuted(UIExecutionContext executionContext,
            Set<CommandExecutionListener> listeners)
   {
      for (CommandExecutionListener listener : listeners)
      {
         if (listener instanceof WizardExecutionListener)
         {
            ((WizardExecutionListener) listener).preWizardExecuted((UIWizard) initialCommand, executionContext);
         }
      }
   }

   protected void firePostWizardFailure(UIExecutionContext executionContext,
            Set<CommandExecutionListener> listeners, Exception e)
   {
      for (CommandExecutionListener listener : listeners)
      {
         if (listener instanceof WizardExecutionListener)
         {
            ((WizardExecutionListener) listener).postWizardFailure((UIWizard) initialCommand, executionContext, e);
         }
      }
   }

   protected void firePostWizardExecuted(UIExecutionContext executionContext,
            Set<CommandExecutionListener> listeners, Result currentResult)
   {
      for (CommandExecutionListener listener : listeners)
      {
         if (listener instanceof WizardExecutionListener)
         {
            ((WizardExecutionListener) listener).postWizardExecuted((UIWizard) initialCommand, executionContext,
                     currentResult);
         }
      }
   }

   @Override
   public List<UIMessage> validate()
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
   public InputComponent<?, ?> getInput(String inputName)
   {
      return getInputs().get(inputName);
   }

   @Override
   public boolean hasInput(String inputName)
   {
      return getInputs().containsKey(inputName);
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
      subflow.clear();
      flow.clear();
      usedSubflows.clear();
   }

   @Override
   public boolean canMoveToNextStep()
   {
      assertInitialized();
      if (!isValid())
      {
         return false;
      }
      NavigationResultEntry[] next = getNextFrom(getCurrentController().getCommand());
      return ((next != null || !subflow.isEmpty()) || usedSubflows.contains(flowPointer));
   }

   @Override
   public boolean canMoveToPreviousStep()
   {
      assertInitialized();
      return flowPointer > 0;
   }

   @Override
   public boolean canExecute()
   {
      assertInitialized();
      // FORGE-1466: Eager initialization so canExecute() works
      refreshFlow();
      for (WizardStepEntry entry : flow)
      {
         if (!entry.controller.canExecute())
         {
            return false;
         }
      }

      // Checking if there is any next page left
      CommandController lastController = flow.get(flow.size() - 1).controller;
      if (lastController.isInitialized())
      {
         NavigationResultEntry[] next = getNextFrom(flow.get(flow.size() - 1).controller.getCommand());
         if (next != null || !subflow.isEmpty())
         {
            return false;
         }
      }
      else
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
      NavigationResultEntry[] result = getNextFrom(currentEntry.controller.getCommand());
      if (nextEntry == null)
      {
         currentEntry.next = result;
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
                  command = subflow.peek().controller.getCommand();
               }
            }
            else
            {
               command = createCommand(result[0]);
            }
            if (command != null)
            {
               CommandController ctrl = controllerFactory.doCreateSingleController(context, runtime, command);
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
    * Remove stale pages in case of navigational changes
    */

   private synchronized void cleanSubsequentStalePages()
   {
      // FIXME: Workaround until FORGE-1704 is fixed
      if (flowPointer == 0)
      {
         flow.subList(1, flow.size()).clear();
         subflow.clear();
      }
      else
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
   }

   /**
    * @param result
    */
   private void addNextFlowStep(NavigationResultEntry[] result)
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
            usedSubflows.add(flowPointer);
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

   @Override
   public List<UICommandMetadata> getWizardStepsMetadata()
   {
      List<UICommandMetadata> stepsMetadata = new ArrayList<>();
      // Do not change anything in the original UIContext
      final Map<Object, Object> attributeMap = new HashMap<>(context.getAttributeMap());
      UIContext safeContext = new DelegatingUIContext(context, context.getProvider())
      {
         @Override
         public Map<Object, Object> getAttributeMap()
         {
            return attributeMap;
         }

         @Override
         public <SELECTIONTYPE> void setSelection(UISelection<SELECTIONTYPE> selection)
         {
            // Do nothing
         }

         @Override
         public <SELECTIONTYPE> void setSelection(SELECTIONTYPE resource)
         {
            // Do nothing
         }
      };
      addCommandMetadata(initialCommand, safeContext, stepsMetadata);
      return stepsMetadata;
   }

   private void addCommandMetadata(UICommand command, UIContext context, List<UICommandMetadata> stepsMetadata)
   {
      UIBuilderImpl builder = new UIBuilderImpl(context, inputComponentFactory);
      try
      {
         command.initializeUI(builder);
      }
      catch (Exception e)
      {
         logger.log(Level.SEVERE, "Error while initializing " + command, e);
      }
      // Empty inputs are not rendered
      if (builder.getInputs().size() > 0)
      {
         stepsMetadata.add(command.getMetadata(context));
      }
      NavigationResultEntry[] nextEntries = getNextFrom(command);
      if (nextEntries != null)
      {
         for (NavigationResultEntry nextEntry : nextEntries)
         {
            UICommand nextCommand = nextEntry.getCommand(addonRegistry, context);
            addCommandMetadata(nextCommand, context, stepsMetadata);
         }
      }
   }

   protected int getFlowPointer()
   {
      return flowPointer;
   }

   protected void setFlowPointer(int flowPointer)
   {
      this.flowPointer = flowPointer;
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

   private WizardStepEntry createEntry(NavigationResultEntry entry, boolean subflowHead)
   {
      UICommand command = createCommand(entry);
      return createEntry(command, subflowHead);
   }

   private UICommand createCommand(NavigationResultEntry entry)
   {
      UICommand command = entry.getCommand(addonRegistry, context);
      return command;
   }

   private WizardStepEntry createEntry(UICommand command, boolean subflowHead)
   {
      CommandController controller = controllerFactory.doCreateSingleController(context, runtime, command);
      return new WizardStepEntry(controller, subflowHead);
   }

   private NavigationResultEntry[] getNextFrom(UICommand command)
   {
      NavigationResult next = null;
      UINavigationContextImpl navigationContext = new UINavigationContextImpl(context, initialCommand, command);
      if (command instanceof UIWizard)
      {
         try
         {
            next = ((UIWizard) command).next(navigationContext);
         }
         catch (Exception e)
         {
            logger.log(Level.SEVERE, "Cannot fetch the next steps from " + command, e);
            next = null;
         }
      }
      // Transform the existing NavigationResult
      Set<NavigationResultTransformer> transformers = new TreeSet<>((o1, o2) -> o2.priority() - o1.priority());
      transformers.addAll(Lists.toList(addonRegistry.getServices(NavigationResultTransformer.class)));
      for (NavigationResultTransformer transformer : transformers)
      {
         if (transformer.handles(navigationContext))
         {
            next = transformer.transform(navigationContext, next);
         }
      }
      return next == null ? null : next.getNext();
   }

   private static class WizardStepEntry
   {
      final CommandController controller;
      NavigationResultEntry[] next;
      // If this entry starts a subflow
      final boolean subflowHead;

      public WizardStepEntry(CommandController controller, boolean subflowHead)
      {
         this.controller = controller;
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