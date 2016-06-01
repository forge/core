/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.controller;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.ui.command.AbstractCommandExecutionListener;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.controller.mock.FlowExampleStep;
import org.jboss.forge.addon.ui.controller.mock.FlowExampleWizard;
import org.jboss.forge.addon.ui.impl.mock.MockUIContext;
import org.jboss.forge.addon.ui.impl.mock.MockUIRuntime;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.wizard.UIWizard;
import org.jboss.forge.addon.ui.wizard.WizardExecutionListener;
import org.jboss.forge.arquillian.AddonDeployment;
import org.jboss.forge.arquillian.AddonDeployments;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class CommandExecutionListenerTest
{
   @Deployment
   @AddonDeployments({
            @AddonDeployment(name = "org.jboss.forge.addon:ui"),
            @AddonDeployment(name = "org.jboss.forge.furnace.container:cdi") })
   public static AddonArchive getDeployment()
   {
      AddonArchive archive = ShrinkWrap
               .create(AddonArchive.class)
               .addBeansXML()
               .addClasses(FlowExampleWizard.class, FlowExampleStep.class, WizardTestListener.class,
                        BogusListener.class, ExecutionEvent.class)
               .addPackage(MockUIContext.class.getPackage())
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui"),
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"));

      return archive;
   }

   @Inject
   private CommandControllerFactory controllerFactory;

   @Inject
   private FlowExampleWizard flowExampleWizard;

   @Test
   public void testWizardExecutionListener() throws Exception
   {
      MockUIContext context = new MockUIContext();
      WizardTestListener wizardListener = new WizardTestListener();
      context.addCommandExecutionListener(wizardListener);

      try (CommandController controller = controllerFactory.createWizardController(context, new MockUIRuntime(),
               flowExampleWizard))
      {
         controller.initialize();
         Assert.assertTrue(controller.canExecute());
         controller.execute();
      }
      ExecutionEvent[] events = wizardListener.getEvents();
      ExecutionEvent[] expected = { ExecutionEvent.PRE_WIZARD, ExecutionEvent.PRE_COMMAND, ExecutionEvent.POST_COMMAND,
               ExecutionEvent.POST_WIZARD };
      Assert.assertArrayEquals(expected, events);
   }

   public enum ExecutionEvent
   {
      PRE_COMMAND, POST_COMMAND, POST_COMMAND_FAILURE, PRE_WIZARD, POST_WIZARD, POST_WIZARD_FAILURE;
   }

   private static class BogusListener extends AbstractCommandExecutionListener
   {
      @Override
      public void preCommandExecuted(UICommand command, UIExecutionContext context)
      {
         throw new RuntimeException("Forcing RuntimeException on preCommandExecuted");
      }

      @Override
      public void postCommandExecuted(UICommand command, UIExecutionContext context, Result result)
      {
         throw new RuntimeException("Forcing Runtime Exception on postCommandExecuted");
      }

      @Override
      public void postCommandFailure(UICommand command, UIExecutionContext context, Throwable failure)
      {
         throw new RuntimeException("Forcing Runtime Exception on postCommandFailure");
      }
   }

   private static class WizardTestListener extends AbstractCommandExecutionListener implements WizardExecutionListener
   {
      private List<ExecutionEvent> events = new ArrayList<>();

      public ExecutionEvent[] getEvents()
      {
         return events.toArray(new ExecutionEvent[events.size()]);
      }

      @Override
      public void preWizardExecuted(UIWizard wizard, UIExecutionContext context)
      {
         events.add(ExecutionEvent.PRE_WIZARD);
      }

      @Override
      public void postWizardExecuted(UIWizard wizard, UIExecutionContext context, Result result)
      {
         events.add(ExecutionEvent.POST_WIZARD);
      }

      @Override
      public void postWizardFailure(UIWizard wizard, UIExecutionContext context, Throwable failure)
      {
         events.add(ExecutionEvent.POST_WIZARD_FAILURE);
      }

      @Override
      public void preCommandExecuted(UICommand command, UIExecutionContext context)
      {
         events.add(ExecutionEvent.PRE_COMMAND);
      }

      @Override
      public void postCommandExecuted(UICommand command, UIExecutionContext context, Result result)
      {
         events.add(ExecutionEvent.POST_COMMAND);
      }

      @Override
      public void postCommandFailure(UICommand command, UIExecutionContext context, Throwable failure)
      {
         events.add(ExecutionEvent.POST_COMMAND_FAILURE);
      }
   }
}
