/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.ui.controller.WizardCommandController;
import org.jboss.forge.addon.ui.impl.mock.MockUIContextListener;
import org.jboss.forge.addon.ui.test.UITestHarness;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class UIContextListenerTest
{
   @Deployment
   @Dependencies({ @AddonDependency(name = "org.jboss.forge.addon:ui-test-harness"),
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi") })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addClasses(MyFirstWizard.class, MockUIContextListener.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui-test-harness"),
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"));

      return archive;
   }

   @Inject
   private MockUIContextListener listener;

   @Inject
   private UITestHarness testHarness;

   @Test
   public void testContextListenerIsSet() throws Exception
   {
      WizardCommandController tester = testHarness.createWizardController(MyFirstWizard.class);
      tester.initialize();
      Assert.assertTrue("Wizard is not on a valid state", tester.isValid());
      Assert.assertTrue("Listener is not set", listener.isContextInitialized());
      tester.execute();
      tester.close();
      Assert.assertFalse("Listener is still set", listener.isContextInitialized());

   }
}
