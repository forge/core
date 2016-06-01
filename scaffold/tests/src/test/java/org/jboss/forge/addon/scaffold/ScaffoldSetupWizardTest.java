/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.scaffold;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.scaffold.mock.MockProvider;
import org.jboss.forge.addon.scaffold.mock.ScaffoldableResourceGenerator;
import org.jboss.forge.addon.scaffold.mock.ScaffoldedResourceGenerator;
import org.jboss.forge.addon.scaffold.spi.ScaffoldProvider;
import org.jboss.forge.addon.scaffold.spi.ScaffoldSetupContext;
import org.jboss.forge.addon.scaffold.ui.ScaffoldSetupWizard;
import org.jboss.forge.addon.ui.controller.WizardCommandController;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.test.UITestHarness;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.container.simple.Service;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.furnace.services.Imported;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * A test class based on the UI Test harness for verifying the behavior of the scaffold setup command.
 */
@RunWith(Arquillian.class)
public class ScaffoldSetupWizardTest
{
   @Deployment
   @AddonDependencies({
            @AddonDependency(name = "org.jboss.forge.addon:projects"),
            @AddonDependency(name = "org.jboss.forge.addon:scaffold"),
            @AddonDependency(name = "org.jboss.forge.addon:maven"),
            @AddonDependency(name = "org.jboss.forge.addon:parser-java"),
            @AddonDependency(name = "org.jboss.forge.addon:ui-test-harness"),
            @AddonDependency(name = "org.jboss.forge.furnace.container:simple")
   })
   public static AddonArchive getDeployment()
   {
      AddonArchive archive = ShrinkWrap
               .create(AddonArchive.class)
               .addPackage(MockProvider.class.getPackage())
               .addClass(ProjectHelper.class)
               .addAsServiceProvider(Service.class, ScaffoldSetupWizardTest.class, ScaffoldableResourceGenerator.class,
                        ScaffoldedResourceGenerator.class, ProjectHelper.class, MockProvider.class);
      return archive;
   }

   private AddonRegistry registry;
   private UITestHarness testHarness;
   private ProjectHelper projectHelper;

   @Before
   public void setUp()
   {
      registry = SimpleContainer.getFurnace(getClass().getClassLoader()).getAddonRegistry();
      projectHelper = SimpleContainer.getServices(getClass().getClassLoader(), ProjectHelper.class).get();
      testHarness = SimpleContainer.getServices(getClass().getClassLoader(), UITestHarness.class).get();
   }

   @Test
   public void testScaffoldSetup() throws Exception
   {
      Project project = projectHelper.createWebProject();

      Imported<ScaffoldProvider> providerInstances = registry.getServices(ScaffoldProvider.class);
      ScaffoldProvider scaffoldProvider = providerInstances.get();
      Assert.assertFalse(scaffoldProvider.isSetup(new ScaffoldSetupContext("", project)));

      try (WizardCommandController c = testHarness.createWizardController(ScaffoldSetupWizard.class, project.getRoot()))
      {
         c.initialize();
         c.setValueFor("provider", "Mock Scaffold Provider");
         c.setValueFor("webRoot", "");
         Assert.assertTrue(c.isValid());
         // Force the resolution of the next step. Without this ScaffoldSetupWizardImpl.next() is not evaluated.
         Assert.assertFalse(c.canMoveToNextStep());
         Result result = c.execute();

         // Verify successful execution
         Assert.assertNotNull(result);
         Assert.assertFalse(result instanceof Failed);

         // Verify that the scaffold was setup
         Assert.assertTrue(scaffoldProvider.isSetup(new ScaffoldSetupContext("", project)));
      }
   }
}
