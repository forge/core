package org.jboss.forge.projects.impl;

/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.Addon;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.container.AddonDependency;
import org.jboss.forge.container.AddonId;
import org.jboss.forge.resource.DirectoryResource;
import org.jboss.forge.ui.UIBuilder;
import org.jboss.forge.ui.UIContext;
import org.jboss.forge.ui.UIInputComponent;
import org.jboss.forge.ui.UISelection;
import org.jboss.forge.ui.UIValidationContext;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class NewProjectCommandTest
{
   @Deployment
   @Dependencies({
            @Addon(name = "org.jboss.forge:projects", version = "2.0.0-SNAPSHOT")
   })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addAsManifestResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml"))
               .addAsAddonDependencies(
                        AddonDependency.create(AddonId.from("org.jboss.forge:resources", "2.0.0-SNAPSHOT")),
                        AddonDependency.create(AddonId.from("org.jboss.forge:projects", "2.0.0-SNAPSHOT")),
                        AddonDependency.create(AddonId.from("org.jboss.forge:ui", "2.0.0-SNAPSHOT"))
               );

      return archive;
   }

   @Inject
   private NewProjectCommand command;

   @Test
   public void testInjectionNotNull()
   {
      Assert.assertNotNull(command);
   }

   @Test
   public void testInvokeCommand() throws Exception
   {
      final List<UIInputComponent<?, ?>> inputs = new ArrayList<UIInputComponent<?, ?>>();

      final UIContext context = new UIContext()
      {
         @Override
         public <T> UISelection<T> getInitialSelection()
         {
            return null;
         }
      };
      final UIBuilder builder = new UIBuilder()
      {
         @Override
         public UIBuilder add(UIInputComponent<?, ?> input)
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

      command.initializeUI(builder);
      command.getNamed().setValue("test");

      command.validate(new UIValidationContext()
      {
         @Override
         public UIContext getUIContext()
         {
            return context;
         }

         @Override
         public void addValidationError(UIInputComponent<?, ?> input, String errorMessage)
         {
         }
      });

      DirectoryResource targetDirectory = command.getTargetLocation().getValue().getChildDirectory("test");

      Assert.assertFalse(targetDirectory.exists());
      command.execute(context);
      Assert.assertTrue(targetDirectory.exists());

      targetDirectory.delete(true);
   }
}
