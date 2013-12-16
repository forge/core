package org.jboss.forge.addon.projects.impl;

/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.projects.BuildSystem;
import org.jboss.forge.addon.projects.mock.MockBuildSystem;
import org.jboss.forge.addon.projects.mock.MockBuildSystem2;
import org.jboss.forge.addon.projects.mock.MockProjectType;
import org.jboss.forge.addon.projects.mock.MockProjectType2;
import org.jboss.forge.addon.projects.mock.MockProjectType3;
import org.jboss.forge.addon.projects.mock.MockProjectTypeUnsatisfied;
import org.jboss.forge.addon.projects.ui.NewProjectWizard;
import org.jboss.forge.addon.ui.controller.WizardCommandController;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.impl.util.Iterators;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.furnace.util.OperatingSystemUtils;
import org.jboss.forge.ui.test.UITestHarness;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class NewProjectWizardBuildSystem2Test
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
               .addClass(MockProjectTypeUnsatisfied.class)
               .addClass(MockProjectType.class)
               .addClass(MockProjectType2.class)
               .addClass(MockProjectType3.class)
               .addClass(MockBuildSystem.class)
               .addClass(MockBuildSystem2.class)
               .addClass(Iterators.class)
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

   @SuppressWarnings("unchecked")
   @Test
   public void testProjectTypeSwitching() throws Exception
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
         Assert.assertEquals("buildsystem", InputComponents.getValueFor(wizard.getInputs().get("buildSystem"))
                  .toString());

         wizard.setValueFor("type", "mock2");
         Assert.assertEquals("buildsystem2", InputComponents.getValueFor(wizard.getInputs().get("buildSystem"))
                  .toString());
         UISelectOne<BuildSystem> buildSystems = (UISelectOne<BuildSystem>) wizard.getInputs().get("buildSystem");
         List<BuildSystem> choices = Iterators.asList(buildSystems.getValueChoices());
         Assert.assertEquals(1, choices.size());

         wizard.setValueFor("type", "mock3");
         choices = Iterators.asList(buildSystems.getValueChoices());
         Assert.assertEquals(2, choices.size());

         wizard.setValueFor("type", "mock2");
         choices = Iterators.asList(buildSystems.getValueChoices());
         Assert.assertEquals(1, choices.size());
      }
      finally
      {
         tempDir.delete();
      }
   }

   @Test
   public void testProjectTypeRestrictedByBuildSystem() throws Exception
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
         wizard.setValueFor("type", "unsatisfied");
         Assert.assertNotEquals("unsatisfied", InputComponents.getValueFor(wizard.getInputs().get("type")).toString());
      }
      finally
      {
         tempDir.delete();
      }
   }
}