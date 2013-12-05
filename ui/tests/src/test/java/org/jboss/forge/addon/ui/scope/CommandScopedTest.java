/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.scope;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.furnace.services.Imported;
import org.jboss.forge.ui.test.WizardTester;
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
   @Dependencies({ @AddonDependency(name = "org.jboss.forge.addon:ui-test-harness"),
      @AddonDependency(name = "org.jboss.forge.furnace.container:cdi") })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
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
   private WizardTester<WizardWithScopedObject> wizardTester;

   @Test
   public void testEnabled()
   {
      Assert.assertTrue(wizardTester.isEnabled());
   }

   @Test
   public void testCommandScope() throws Exception
   {
      wizardTester.launch();
      Assert.assertTrue(wizardTester.isValid());
      CommandScopedModel model = modelInstance.get();
      Assert.assertNull(model.getName());
      wizardTester.setValueFor("firstName", "Forge");
      Assert.assertNotNull(model);
      Assert.assertFalse(wizardTester.canFlipToNextPage());
      Assert.assertEquals("Forge", model.getName());
      wizardTester.finish(null);
   }

   @Test
   @org.junit.Ignore("FORGE-1209")
   public void testImportedWithCustomScope() throws Exception
   {
      wizardTester.launch();
      Assert.assertTrue("Should not be satisfied since there is no Context in scope", modelInstance.isUnsatisfied());
      Assert.assertTrue(wizardTester.isValid());
      Assert.assertFalse("Should be satisfied since there command context was initialized",
               modelInstance.isUnsatisfied());
      wizardTester.finish(null);
      Assert.assertTrue("Should not be satisfied since there is no Context in scope after finish is called",
               modelInstance.isUnsatisfied());
   }
}
