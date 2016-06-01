/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.scope;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.ui.controller.WizardCommandController;
import org.jboss.forge.addon.ui.test.UITestHarness;
import org.jboss.forge.arquillian.AddonDeployment;
import org.jboss.forge.arquillian.AddonDeployments;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.furnace.services.Imported;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class CommandScopedTest
{
   @Deployment
   @AddonDeployments({ @AddonDeployment(name = "org.jboss.forge.addon:ui-test-harness"),
            @AddonDeployment(name = "org.jboss.forge.furnace.container:cdi") })
   public static AddonArchive getDeployment()
   {
      AddonArchive archive = ShrinkWrap
               .create(AddonArchive.class)
               .addClasses(WizardWithScopedObject.class, CommandScopedModel.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui-test-harness"),
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"));

      return archive;
   }

   @Inject
   private Imported<CommandScopedModel> modelInstance;

   @Inject
   private UITestHarness testHarness;

   @Test
   public void testCommandScope() throws Exception
   {
      WizardCommandController tester = testHarness.createWizardController(WizardWithScopedObject.class);
      tester.initialize();
      Assert.assertTrue(tester.isValid());
      CommandScopedModel model = modelInstance.get();
      Assert.assertNull(model.getName());
      tester.setValueFor("firstName", "Forge");
      Assert.assertNotNull(model);
      Assert.assertFalse(tester.canMoveToNextStep());
      Assert.assertEquals("Forge", model.getName());
      tester.execute();
   }

   @Test
   @org.junit.Ignore("FORGE-1209")
   public void testImportedWithCustomScope() throws Exception
   {
      WizardCommandController tester = testHarness.createWizardController(WizardWithScopedObject.class);
      tester.initialize();
      Assert.assertTrue("Should not be satisfied since there is no Context in scope", modelInstance.isUnsatisfied());
      Assert.assertTrue(tester.isValid());
      Assert.assertFalse("Should be satisfied since there command context was initialized",
               modelInstance.isUnsatisfied());
      tester.execute();
      Assert.assertTrue("Should not be satisfied since there is no Context in scope after finish is called",
               modelInstance.isUnsatisfied());
   }
}
