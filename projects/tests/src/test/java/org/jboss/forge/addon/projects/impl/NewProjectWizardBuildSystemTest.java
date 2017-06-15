/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.projects.impl;

/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

import java.io.File;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.projects.mock.MockBuildSystem;
import org.jboss.forge.addon.projects.mock.MockMetadataFacet;
import org.jboss.forge.addon.projects.mock.MockProject;
import org.jboss.forge.addon.projects.mock.MockProjectTypeNoRequiredFacets;
import org.jboss.forge.addon.projects.mock.MockProjectTypeNullRequiredFacets;
import org.jboss.forge.addon.projects.ui.NewProjectWizard;
import org.jboss.forge.addon.ui.controller.WizardCommandController;
import org.jboss.forge.addon.ui.test.UITestHarness;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.container.simple.Service;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.furnace.util.OperatingSystemUtils;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class NewProjectWizardBuildSystemTest
{
   @Deployment
   @AddonDependencies({
            @AddonDependency(name = "org.jboss.forge.furnace.container:simple"),
            @AddonDependency(name = "org.jboss.forge.addon:projects"),
            @AddonDependency(name = "org.jboss.forge.addon:ui-test-harness")
   })
   public static AddonArchive getDeployment()
   {
      AddonArchive archive = ShrinkWrap
               .create(AddonArchive.class)
               .addClass(MockProjectTypeNoRequiredFacets.class)
               .addClass(MockProjectTypeNullRequiredFacets.class)
               .addClass(MockBuildSystem.class)
               .addClass(MockProject.class)
               .addClass(MockMetadataFacet.class)
               .addAsServiceProvider(Service.class, NewProjectWizardBuildSystemTest.class,
                        MockProjectTypeNoRequiredFacets.class,
                        MockProjectTypeNullRequiredFacets.class, MockBuildSystem.class, MockMetadataFacet.class);

      return archive;
   }

   private UITestHarness testHarness;

   @Before
   public void setUp()
   {
      testHarness = SimpleContainer.getServices(getClass().getClassLoader(), UITestHarness.class).get();
   }

   @Test
   public void testProjectTypeWithNoBuildSystemRequirements() throws Exception
   {
      File tempDir = OperatingSystemUtils.createTempDir();
      try (WizardCommandController wizard = testHarness.createWizardController(NewProjectWizard.class))
      {
         wizard.initialize();
         Assert.assertFalse(wizard.canMoveToNextStep());
         wizard.setValueFor("named", "test");
         wizard.setValueFor("targetLocation", tempDir);
         wizard.setValueFor("topLevelPackage", "org.example");
         wizard.setValueFor("type", "norequirements");
         Assert.assertEquals("norequirements", InputComponents.getValueFor(wizard.getInputs().get("type")).toString());
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
      try (WizardCommandController wizard = testHarness.createWizardController(NewProjectWizard.class))
      {
         wizard.initialize();
         Assert.assertFalse(wizard.canMoveToNextStep());
         wizard.setValueFor("named", "test");
         wizard.setValueFor("targetLocation", tempDir);
         wizard.setValueFor("topLevelPackage", "org.example");
         wizard.setValueFor("type", "nullrequirements");
         Assert.assertEquals("nullrequirements", InputComponents.getValueFor(wizard.getInputs().get("type"))
                  .toString());
      }
      finally
      {
         tempDir.delete();
      }
   }
}