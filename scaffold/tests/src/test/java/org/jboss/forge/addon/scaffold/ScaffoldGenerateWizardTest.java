package org.jboss.forge.addon.scaffold;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.scaffold.mock.MockProvider;
import org.jboss.forge.addon.scaffold.spi.ScaffoldProvider;
import org.jboss.forge.addon.scaffold.spi.ScaffoldSetupContext;
import org.jboss.forge.addon.scaffold.ui.ScaffoldGenerateCommand;
import org.jboss.forge.addon.scaffold.ui.ScaffoldSetupWizard;
import org.jboss.forge.addon.ui.controller.WizardCommandController;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.test.UITestHarness;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.furnace.services.Imported;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * A test class based on the UI Test harness for verifying the behavior of the scaffold generate command.
 */
@RunWith(Arquillian.class)
public class ScaffoldGenerateWizardTest
{
   @Deployment
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.addon:projects"),
            @AddonDependency(name = "org.jboss.forge.addon:scaffold"),
            @AddonDependency(name = "org.jboss.forge.addon:maven"),
            @AddonDependency(name = "org.jboss.forge.addon:parser-java"),
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi"),
            @AddonDependency(name = "org.jboss.forge.addon:ui-test-harness")
   })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addPackage(MockProvider.class.getPackage())
               .addClass(ProjectHelper.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.addon:projects"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:scaffold"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:maven"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:parser-java"),
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui-test-harness")
               );

      return archive;
   }

   @Inject
   private AddonRegistry registry;

   @Inject
   private UITestHarness testHarness;

   @Inject
   private ProjectHelper projectHelper;

   @Test
   public void testScaffoldGenerate() throws Exception
   {
      Project project = projectHelper.createWebProject();
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
      }
      try (WizardCommandController c = testHarness.createWizardController(ScaffoldGenerateCommand.class, project.getRoot()))
      {
         c.initialize();
         c.setValueFor("provider", "Mock Scaffold Provider");
         c.setValueFor("webRoot", "");
         Assert.assertTrue(c.isValid());
         // Force the resolution of the next step. Without this ScaffoldGenerateCommandImpl.next() is not evaluated.
         Assert.assertFalse(c.canMoveToNextStep());
         Result result = c.execute();

         // Verify successful execution
         Assert.assertNotNull(result);
         Assert.assertFalse(result instanceof Failed);
      }
   }

   @Test
   public void testScaffoldGenerateWithSetup() throws Exception
   {
      Project project = projectHelper.createWebProject();

      Imported<ScaffoldProvider> providerInstances = registry.getServices(ScaffoldProvider.class);
      ScaffoldProvider scaffoldProvider = providerInstances.get();
      Assert.assertFalse(scaffoldProvider.isSetup(new ScaffoldSetupContext("", project)));

      try (WizardCommandController c = testHarness.createWizardController(ScaffoldGenerateCommand.class,
               project.getRoot()))
      {
         c.initialize();
         c.setValueFor("provider", "Mock Scaffold Provider");
         c.setValueFor("webRoot", "");
         Assert.assertTrue(c.isValid());
         // Force the resolution of the next step. Without this ScaffoldGenerateCommandImpl.next() is not evaluated.
         Assert.assertFalse(c.canMoveToNextStep());
         Result result = c.execute();

         // Verify successful execution
         Assert.assertNotNull(result);
         Assert.assertFalse(result instanceof Failed);

         // Verify that the scaffold was setup
         Assert.assertTrue(scaffoldProvider.isSetup(new ScaffoldSetupContext("", project)));
         // Verify that the scaffold was generated
         Assert.assertTrue(((MockProvider) scaffoldProvider).isGenerated());
      }
   }
}
