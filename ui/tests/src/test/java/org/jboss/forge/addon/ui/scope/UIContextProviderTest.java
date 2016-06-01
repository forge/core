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
import org.jboss.forge.addon.ui.context.UIContextProvider;
import org.jboss.forge.addon.ui.controller.WizardCommandController;
import org.jboss.forge.addon.ui.test.UITestHarness;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.services.Imported;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests the {@link UIContextProvider} feature
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class UIContextProviderTest
{
   @Deployment
   @AddonDependencies({
            @AddonDependency(name = "org.jboss.forge.addon:ui-test-harness"),
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi")
   })
   public static AddonArchive getDeployment()
   {
      AddonArchive archive = ShrinkWrap
               .create(AddonArchive.class)
               .addClasses(WizardWithScopedObject.class, CommandScopedModel.class)
               .addBeansXML();
      return archive;
   }

   @Inject
   private UIContextProvider contextProvider;

   @Inject
   private Imported<UIContextProvider> imported;

   @Inject
   private UITestHarness uiTestHarness;

   @Test
   public void testContextProvider() throws Exception
   {
      Assert.assertFalse(imported.isUnsatisfied());
      Assert.assertNull(contextProvider.getUIContext());
      try (WizardCommandController controller = uiTestHarness.createWizardController(WizardWithScopedObject.class))
      {
         controller.initialize();
         Assert.assertNotNull(contextProvider.getUIContext());
         Assert.assertSame(contextProvider.getUIContext(), controller.getContext());
      }
      Assert.assertNull(contextProvider.getUIContext());
   }
}
