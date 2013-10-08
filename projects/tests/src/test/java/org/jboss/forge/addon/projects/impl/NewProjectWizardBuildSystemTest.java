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
import org.jboss.forge.addon.projects.mock.MockProjectTypeNoRequiredFacets;
import org.jboss.forge.addon.projects.mock.MockProjectTypeNullRequiredFacets;
import org.jboss.forge.addon.projects.ui.NewProjectWizard;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.furnace.util.OperatingSystemUtils;
import org.jboss.forge.ui.test.WizardTester;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class NewProjectWizardBuildSystemTest
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
               .addClass(MockProjectTypeNoRequiredFacets.class)
               .addClass(MockProjectTypeNullRequiredFacets.class)
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
   private WizardTester<NewProjectWizard> wizard;

   @Test
   public void testProjectTypeWithNoBuildSystemRequirements() throws Exception
   {
      File tempDir = OperatingSystemUtils.createTempDir();
      try
      {
         wizard.launch();
         Assert.assertFalse(wizard.canFlipToNextPage());
         wizard.setValueFor("named", "test");
         wizard.setValueFor("targetLocation", tempDir);
         wizard.setValueFor("topLevelPackage", "org.example");
         wizard.setValueFor("type", "norequirements");
         Assert.assertEquals("norequirements", InputComponents.getValueFor(wizard.getInputComponent("type")).toString());
      }
      finally
      {
         tempDir.delete();
      }
   }

   @Test
   public void testProjectTypeWithNullBuildSystemRequirements() throws Exception
   {
      File tempDir = OperatingSystemUtils.createTempDir();
      try
      {
         wizard.launch();
         Assert.assertFalse(wizard.canFlipToNextPage());
         wizard.setValueFor("named", "test");
         wizard.setValueFor("targetLocation", tempDir);
         wizard.setValueFor("topLevelPackage", "org.example");
         wizard.setValueFor("type", "nullrequirements");
         Assert.assertEquals("nullrequirements", InputComponents.getValueFor(wizard.getInputComponent("type"))
                  .toString());
      }
      finally
      {
         tempDir.delete();
      }
   }
}