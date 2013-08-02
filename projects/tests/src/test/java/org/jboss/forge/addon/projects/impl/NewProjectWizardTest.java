package org.jboss.forge.addon.projects.impl;

/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.ui.context.AbstractUIContext;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.util.Selections;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class NewProjectWizardTest
{
   @Deployment
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.addon:projects", version = "2.0.0-SNAPSHOT"),
            @AddonDependency(name = "org.jboss.forge.addon:ui", version = "2.0.0-SNAPSHOT")
   })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi", "2.0.0-SNAPSHOT"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:resources", "2.0.0-SNAPSHOT"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:projects", "2.0.0-SNAPSHOT"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui", "2.0.0-SNAPSHOT")
               );

      return archive;
   }

   @Inject
   private NewProjectWizard command;

   @Inject
   private NewProjectWizard command2;

   @Inject
   private ResourceFactory factory;

   @Test
   public void testInjectionNotNull()
   {
      Assert.assertNotNull(command);
   }

   @Test
   public void testInvokeCommand() throws Exception
   {
      final List<InputComponent<?, ?>> inputs = new ArrayList<InputComponent<?, ?>>();

      final UIContext context = new AbstractUIContext()
      {
         @Override
         public <SELECTIONTYPE> UISelection<SELECTIONTYPE> getInitialSelection()
         {
            return Selections.emptySelection();
         }
      };
      final UIBuilder builder = new UIBuilder()
      {
         @Override
         public UIBuilder add(InputComponent<?, ?> input)
         {
            inputs.add(input);
            return this;
         }

         @Override
         public UIContext getUIContext()
         {
            return context;
         }
      };

      File tempDir = File.createTempFile("forge", "projectTests");
      tempDir.delete();

      try
      {
         command.initializeUI(builder);

         Assert.assertFalse(command.getOverwrite().isEnabled());

         command.getTargetLocation().setValue(factory.create(DirectoryResource.class, tempDir));
         command.getNamed().setValue("test");

         Assert.assertFalse(command.getOverwrite().isEnabled());

         command.getTopLevelPackage().setValue("org.example");

         command.validate(new UIValidationContext()
         {
            @Override
            public UIContext getUIContext()
            {
               return context;
            }

            @Override
            public void addValidationError(InputComponent<?, ?> input, String errorMessage)
            {
            }
         });

         DirectoryResource targetDirectory = command.getTargetLocation().getValue().getChildDirectory("test");

         Assert.assertFalse(targetDirectory.exists());
         command.execute(context);
         Assert.assertTrue(targetDirectory.exists());

         targetDirectory.delete(true);
      }
      finally
      {
         tempDir.delete();
      }
   }

   @Test
   public void testOverwriteEnabledWhenTargetDirectoryExistsNotEmpty() throws Exception
   {
      final List<InputComponent<?, ?>> inputs = new ArrayList<InputComponent<?, ?>>();

      final UIContext context = new AbstractUIContext()
      {
         @Override
         public <SELECTIONTYPE> UISelection<SELECTIONTYPE> getInitialSelection()
         {
            return Selections.emptySelection();
         }
      };
      final UIBuilder builder = new UIBuilder()
      {
         @Override
         public UIBuilder add(InputComponent<?, ?> input)
         {
            inputs.add(input);
            return this;
         }

         @Override
         public UIContext getUIContext()
         {
            return context;
         }
      };

      File tempDir = File.createTempFile("forge", "projectTests");
      tempDir.delete();
      new File(tempDir, "test/something").mkdirs();

      try
      {
         command2.initializeUI(builder);

         Assert.assertFalse(command2.getOverwrite().isEnabled());

         command2.getTargetLocation().setValue(factory.create(DirectoryResource.class, tempDir));
         command2.getNamed().setValue("test");

         Assert.assertTrue(command2.getOverwrite().isEnabled());
      }
      finally
      {
         tempDir.delete();
      }
   }
}