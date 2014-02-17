/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.command;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.controller.mock.ExampleCommand;
import org.jboss.forge.addon.ui.controller.mock.ExampleNoUICommand;
import org.jboss.forge.addon.ui.controller.mock.FlowExampleStep;
import org.jboss.forge.addon.ui.example.commands.ExampleAnnotatedCommand;
import org.jboss.forge.addon.ui.impl.mock.MockUIContext;
import org.jboss.forge.addon.ui.impl.mock.MockUIRuntime;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
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
   @Dependencies({ @AddonDependency(name = "org.jboss.forge.addon:ui"),
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi") })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addClasses(ExampleCommand.class, ExampleNoUICommand.class, ExampleAnnotatedCommand.class,
                        FlowExampleStep.class)
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
                  );
         count++;
      }

      Assert.assertEquals(4, count);
   }

}
