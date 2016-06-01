/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.command;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.controller.mock.ExampleCommand;
import org.jboss.forge.addon.ui.controller.mock.ExampleNoUICommand;
import org.jboss.forge.addon.ui.controller.mock.FlowExampleStep;
import org.jboss.forge.addon.ui.example.commands.ExampleAnnotatedCommand;
import org.jboss.forge.addon.ui.impl.mock.MockUIRuntime;
import org.jboss.forge.addon.ui.test.impl.UIContextImpl;
import org.jboss.forge.addon.ui.util.Selections;
import org.jboss.forge.arquillian.AddonDeployment;
import org.jboss.forge.arquillian.AddonDeployments;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.furnace.util.Lists;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test class for the {@link CommandController} feature
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class CommandFactoryExceptionTest
{
   @Deployment
   @AddonDeployments({ @AddonDeployment(name = "org.jboss.forge.addon:ui"),
            @AddonDeployment(name = "org.jboss.forge.addon:ui-test-harness") })
   public static AddonArchive getDeployment()
   {
      AddonArchive archive = ShrinkWrap
               .create(AddonArchive.class)
               .addClasses(ExceptionCommand.class, ExampleCommand.class, ExampleNoUICommand.class,
                        ExampleAnnotatedCommand.class,
                        FlowExampleStep.class)
               .addPackage(MockUIRuntime.class.getPackage())
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui-test-harness"),
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"));

      return archive;
   }

   @Inject
   private CommandFactory commandFactory;

   @Test
   public void testInjection() throws Exception
   {
      Assert.assertNotNull(commandFactory);
   }

   @Test
   public void testNoExceptionsOnEnabledCommandNames()
   {
      UIContext context = new UIContextImpl(true, Selections.emptySelection());
      Set<String> enabledCommandNames = commandFactory.getEnabledCommandNames(context);
      Assert.assertEquals(5, enabledCommandNames.size());
   }

   @Test
   public void testNoExceptionsOnCommandsNames()
   {
      UIContext context = new UIContextImpl(true, Selections.emptySelection());
      Set<String> commandNames = commandFactory.getCommandNames(context);
      Assert.assertEquals(5, commandNames.size());

   }

   @Test
   public void testNoExceptionsOnUICommands()
   {
      List<UICommand> list = Lists.toList(commandFactory.getCommands());
      Assert.assertEquals(6, list.size());

   }

}
