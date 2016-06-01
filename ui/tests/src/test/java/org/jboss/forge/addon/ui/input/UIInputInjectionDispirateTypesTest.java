/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.input;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.arquillian.AddonDeployment;
import org.jboss.forge.arquillian.AddonDeployments;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.furnace.util.Callables;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class UIInputInjectionDispirateTypesTest
{
   @Deployment
   @AddonDeployments({ @AddonDeployment(name = "org.jboss.forge.addon:ui"),
            @AddonDeployment(name = "org.jboss.forge.furnace.container:cdi") })
   public static AddonArchive getDeployment()
   {
      AddonArchive archive = ShrinkWrap
               .create(AddonArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(AddonDependencyEntry.create("org.jboss.forge.addon:ui"),
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"));

      return archive;
   }

   @Inject
   UIInput<String> firstName;

   @Inject
   UIInput<Boolean> kill;

   @Test
   public void testInjectionNotNull()
   {
      Assert.assertNotNull(firstName);

      Assert.assertNotNull(kill);
   }

   @Test
   public void testInputValues()
   {
      Assert.assertEquals("firstName", firstName.getName());
      Assert.assertEquals(String.class, firstName.getValueType());

      Assert.assertEquals("kill", kill.getName());
      Assert.assertEquals(Boolean.class, kill.getValueType());
   }

   @Test
   public void testRequired()
   {
      firstName.setRequired(true);
      Assert.assertTrue(firstName.isRequired());
      firstName.setRequired(false);
      Assert.assertFalse(firstName.isRequired());

      kill.setRequired(true);
      Assert.assertTrue(kill.isRequired());
      kill.setRequired(false);
      Assert.assertFalse(kill.isRequired());
   }

   @Test
   public void testDefaultValue()
   {
      final String inputVal = "A String";
      firstName.setDefaultValue(inputVal);
      Assert.assertEquals(inputVal, firstName.getValue());

      final String inputVal2 = "Another String";
      firstName.setDefaultValue(Callables.returning(inputVal2));
      Assert.assertEquals(inputVal2, firstName.getValue());

      final Boolean inputVal3 = true;
      kill.setDefaultValue(inputVal3);
      Assert.assertEquals(inputVal3, kill.getValue());

      final Boolean inputVal4 = false;
      kill.setDefaultValue(Callables.returning(inputVal4));
      Assert.assertEquals(inputVal4, kill.getValue());
   }
}
