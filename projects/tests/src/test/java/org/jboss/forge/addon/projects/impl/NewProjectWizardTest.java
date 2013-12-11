package org.jboss.forge.addon.projects.impl;

/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

import java.io.File;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.projects.mock.MockBuildSystem;
import org.jboss.forge.addon.projects.mock.MockProjectType;
import org.jboss.forge.addon.projects.ui.NewProjectWizard;
import org.jboss.forge.addon.ui.controller.WizardCommandController;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.furnace.util.OperatingSystemUtils;
import org.jboss.forge.ui.test.UITestHarness;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class NewProjectWizardTest
{
   @Deployment
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.addon:projects"),
            @AddonDependency(name = "org.jboss.forge.addon:ui-test-harness")
   })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addClass(MockProjectType.class)
               .addClass(MockBuildSystem.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:projects"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui-test-harness")
               );

      return archive;
   }


   @Inject
   private UITestHarness testHarness;

   @Test
   public void testInvokeCommand() throws Exception
   {
      File tempDir = OperatingSystemUtils.createTempDir();
      try
      {
         WizardCommandController wizard = testHarness.createWizardController(NewProjectWizard.class);
         wizard.initialize();
         Assert.assertFalse(wizard.canMoveToNextStep());
         wizard.setValueFor("named", "test");
         wizard.setValueFor("targetLocation", tempDir);
         wizard.setValueFor("topLevelPackage", "org.example");
         wizard.setValueFor("type", "mock");
         Assert.assertTrue(wizard.isValid());
         Assert.assertTrue(wizard.canExecute());
         File targetDirectory = new File(tempDir, "test");
         Assert.assertFalse(targetDirectory.exists());
         wizard.execute();

         Assert.assertTrue(targetDirectory.exists());
      }
      finally
      {
         tempDir.delete();
      }
   }

   @Test
   public void testValidateProjectName() throws Exception
   {
      File tempDir = OperatingSystemUtils.createTempDir();
      try
      {
         WizardCommandController wizard = testHarness.createWizardController(NewProjectWizard.class);
         wizard.initialize();
         Assert.assertFalse(wizard.canMoveToNextStep());
         wizard.setValueFor("named", "Test Project Name Invalid");
         wizard.setValueFor("targetLocation", tempDir);
         wizard.setValueFor("topLevelPackage", "org.example");
         wizard.setValueFor("type", "mock");

         Assert.assertFalse(wizard.isValid());
         Assert.assertFalse(wizard.canExecute());

         wizard.setValueFor("named", "Test-Project-Name-Valid");

         Assert.assertTrue(wizard.isValid());
         Assert.assertTrue(wizard.canExecute());

         File targetDirectory = new File(tempDir, "Test-Project-Name-Valid");
         Assert.assertFalse(targetDirectory.exists());
         wizard.execute();

         Assert.assertTrue(targetDirectory.exists());
      }
      finally
      {
         tempDir.delete();
      }
   }

   @Test
   public void testOverwriteEnabledWhenTargetDirectoryExistsNotEmpty() throws Exception
   {

      File tempDir = OperatingSystemUtils.createTempDir();
      File something = new File(tempDir, "test/something");
      something.mkdirs();
      try
      {
         WizardCommandController wizard = testHarness.createWizardController(NewProjectWizard.class);
         wizard.initialize();
         Assert.assertFalse(wizard.canMoveToNextStep());
         Assert.assertFalse(wizard.getInput("overwrite").isEnabled());
         wizard.setValueFor("named", "test");
         wizard.setValueFor("targetLocation", tempDir);
         wizard.setValueFor("topLevelPackage", "org.example");
         wizard.setValueFor("type", "mock");
         Assert.assertFalse(wizard.isValid());
         Assert.assertTrue(wizard.getInput("overwrite").isEnabled());
         Assert.assertFalse(wizard.canMoveToNextStep());
         Assert.assertFalse(wizard.canExecute());
      }
      finally
      {
         something.delete();
         tempDir.delete();
      }
   }
}