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
import java.util.List;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.projects.ProjectProvider;
import org.jboss.forge.addon.projects.mock.MockBuildSystem;
import org.jboss.forge.addon.projects.mock.MockBuildSystem2;
import org.jboss.forge.addon.projects.mock.MockMetadataFacet;
import org.jboss.forge.addon.projects.mock.MockProject;
import org.jboss.forge.addon.projects.mock.MockProjectType;
import org.jboss.forge.addon.projects.mock.MockProjectType2;
import org.jboss.forge.addon.projects.mock.MockProjectType3;
import org.jboss.forge.addon.projects.mock.MockProjectTypeUnsatisfied;
import org.jboss.forge.addon.projects.ui.NewProjectWizard;
import org.jboss.forge.addon.ui.controller.WizardCommandController;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.test.UITestHarness;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.container.simple.Service;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.furnace.util.Iterators;
import org.jboss.forge.furnace.util.OperatingSystemUtils;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class NewProjectWizardBuildSystem2Test
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
               .addClass(MockProjectTypeUnsatisfied.class)
               .addClass(MockProjectType.class)
               .addClass(MockProjectType2.class)
               .addClass(MockProjectType3.class)
               .addClass(MockBuildSystem.class)
               .addClass(MockBuildSystem2.class)
               .addClass(MockProject.class)
               .addClass(MockMetadataFacet.class)
               .addAsServiceProvider(Service.class, NewProjectWizardBuildSystem2Test.class,
                        MockProjectTypeUnsatisfied.class, MockProjectType.class, MockProjectType2.class,
                        MockProjectType3.class, MockBuildSystem.class, MockBuildSystem2.class, MockMetadataFacet.class);

      return archive;
   }

   private UITestHarness testHarness;

   @Before
   public void setUp()
   {
      testHarness = SimpleContainer.getServices(getClass().getClassLoader(), UITestHarness.class).get();
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testProjectTypeSwitching() throws Exception
   {
      File tempDir = OperatingSystemUtils.createTempDir();
      try (WizardCommandController wizard = testHarness.createWizardController(NewProjectWizard.class))
      {
         wizard.initialize();
         Assert.assertFalse(wizard.canMoveToNextStep());
         wizard.setValueFor("named", "test");
         wizard.setValueFor("targetLocation", tempDir);
         wizard.setValueFor("topLevelPackage", "org.example");
         wizard.setValueFor("type", "mock");
         Assert.assertEquals("buildsystem", InputComponents.getValueFor(wizard.getInput("buildSystem")).toString());

         wizard.setValueFor("type", "mock2");
         Assert.assertEquals("buildsystem2", InputComponents.getValueFor(wizard.getInput("buildSystem"))
                  .toString());
         UISelectOne<ProjectProvider> buildSystems = (UISelectOne<ProjectProvider>) wizard.getInput("buildSystem");
         List<ProjectProvider> choices = Iterators.asList(buildSystems.getValueChoices());
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
      try (WizardCommandController wizard = testHarness.createWizardController(NewProjectWizard.class))
      {
         wizard.initialize();
         Assert.assertFalse(wizard.canMoveToNextStep());
         wizard.setValueFor("named", "test");
         wizard.setValueFor("targetLocation", tempDir);
         wizard.setValueFor("topLevelPackage", "org.example");
         wizard.setValueFor("type", "unsatisfied");
         Assert.assertNotEquals("unsatisfied", InputComponents.getValueFor(wizard.getInput("type")).toString());
      }
      finally
      {
         tempDir.delete();
      }
   }
}