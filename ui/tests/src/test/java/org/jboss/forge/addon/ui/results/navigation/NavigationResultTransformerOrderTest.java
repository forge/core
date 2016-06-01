/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.results.navigation;

import static org.hamcrest.CoreMatchers.instanceOf;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.ui.controller.WizardCommandController;
import org.jboss.forge.addon.ui.test.UITestHarness;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class NavigationResultTransformerOrderTest
{
   @Deployment
   @AddonDependencies
   public static AddonArchive getDeployment()
   {
      return ShrinkWrap.create(AddonArchive.class)
               .addBeansXML()
               .addClasses(FirstNameWizard.class, LastNameCommand.class, AddressCommand.class,
                        AddLastNameTransformer.class, AddAddressTransformer.class);
   }

   @Inject
   private UITestHarness uiTestHarness;

   @Test
   public void shouldHaveAddedAddressCommandAsLastStep() throws Exception
   {
      try (WizardCommandController controller = uiTestHarness.createWizardController(FirstNameWizard.class))
      {
         controller.initialize();
         Assert.assertTrue(controller.canMoveToNextStep());
         controller.next().initialize();
         Assert.assertThat(controller.getCommand(), instanceOf(LastNameCommand.class));
         Assert.assertTrue(controller.canMoveToNextStep());
         controller.next().initialize();
         Assert.assertThat(controller.getCommand(), instanceOf(AddressCommand.class));
         Assert.assertFalse(controller.canMoveToNextStep());
      }
   }
}
