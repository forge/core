/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.command;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.controller.mock.DifferentNameCommand;
import org.jboss.forge.addon.ui.controller.mock.ExampleCommand;
import org.jboss.forge.addon.ui.controller.mock.ExampleNoUICommand;
import org.jboss.forge.addon.ui.controller.mock.FlowExampleStep;
import org.jboss.forge.addon.ui.example.commands.ExampleAnnotatedCommand;
import org.jboss.forge.addon.ui.impl.mock.MockUIContext;
import org.jboss.forge.addon.ui.impl.mock.MockUIRuntime;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.arquillian.AddonDeployment;
import org.jboss.forge.arquillian.AddonDeployments;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
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
public class CommandFactoryTest
{
   @Deployment
   @AddonDeployments({ @AddonDeployment(name = "org.jboss.forge.addon:ui"),
            @AddonDeployment(name = "org.jboss.forge.furnace.container:cdi") })
   public static AddonArchive getDeployment()
   {
      AddonArchive archive = ShrinkWrap
               .create(AddonArchive.class)
               .addClasses(ExampleCommand.class, ExampleNoUICommand.class, ExampleAnnotatedCommand.class,
                        FlowExampleStep.class, DifferentNameCommand.class)
               .addPackage(MockUIRuntime.class.getPackage())
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui"),
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
   public void testGetCommands() throws Exception
   {
      Iterable<UICommand> commands = commandFactory.getCommands();
      Assert.assertNotNull(commands);

      int count = 0;
      for (UICommand command : commands)
      {
         UICommandMetadata metadata = command.getMetadata(new MockUIContext());
         Assert.assertTrue(ExampleCommand.class.equals(metadata.getType())
                  || ExampleNoUICommand.class.equals(metadata.getType())
                  || ExampleAnnotatedCommand.class.equals(metadata.getType())
                  || DifferentNameCommand.class.equals(metadata.getType())
                  );
         count++;
      }

      Assert.assertEquals(6, count);
   }

   @Test
   public void testCommandCache()
   {
      String commandName = ExampleCommand.class.getName();
      MockUIContext context = new MockUIContext();
      UICommand command = commandFactory.getCommandByName(context, commandName);
      Assert.assertNotNull(command);
      Assert.assertSame(command, commandFactory.getCommandByName(context, commandName));
      Assert.assertNotSame(command, commandFactory.getNewCommandByName(context, commandName));
   }

   @Test
   public void testCommandByNameLookup()
   {
      MockUIContext context = new MockUIContext();
      context.getProvider().setGUI(false);
      Assert.assertNotNull(commandFactory.getCommandByName(context, "a-gui-command"));
      Assert.assertNotNull(commandFactory.getCommandByName(context, "a-shell-command"));
      Assert.assertNull(commandFactory.getCommandByName(context, "an-invalid-command"));
      context.getProvider().setGUI(true);
      Assert.assertNotNull(commandFactory.getCommandByName(context, "a-gui-command"));
      Assert.assertNotNull(commandFactory.getCommandByName(context, "a-shell-command"));
      Assert.assertNull(commandFactory.getCommandByName(context, "an-invalid-command"));
   }

}
