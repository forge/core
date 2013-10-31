/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.enhancer;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;

import java.util.Iterator;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.ui.domain.Gender;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.SelectComponentEnhancer;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.ui.test.WizardTester;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test case for {@link SelectComponentEnhancer}
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class SelectComponentEnhancerTest
{
   @Deployment
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.addon:ui"),
            @AddonDependency(name = "org.jboss.forge.addon:ui-test-harness"),
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi")
   })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addClasses(MainWizard.class, NextStepWizard.class, Gender.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui-test-harness"),
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"));

      return archive;
   }

   @Inject
   WizardTester<MainWizard> mainWizard;

   @Test
   public void testEnhancerInjection() throws Exception
   {
      Assert.assertNotNull(mainWizard);
   }

   @Test
   @SuppressWarnings("unchecked")
   public void testEnhancerForGender() throws Exception
   {
      mainWizard.launch();
      Assert.assertTrue(mainWizard.canFlipToNextPage());
      mainWizard.next();
      InputComponent<?, ?> inputComponent = mainWizard.getInputComponent("gender");
      Assert.assertThat(inputComponent, is(instanceOf(UISelectOne.class)));
      UISelectOne<Gender> gender = (UISelectOne<Gender>) inputComponent;
      Iterator<Gender> iterator = gender.getValueChoices().iterator();
      Assert.assertTrue(iterator.hasNext());
      Assert.assertEquals(Gender.FEMALE, iterator.next());
      Assert.assertFalse("Should contain only Gender.FEMALE", iterator.hasNext());
   }

}
