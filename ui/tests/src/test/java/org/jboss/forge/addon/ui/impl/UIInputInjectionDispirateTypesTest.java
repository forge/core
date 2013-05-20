/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.impl;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.arquillian.Addon;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.container.addons.AddonId;
import org.jboss.forge.container.repositories.AddonDependencyEntry;
import org.jboss.forge.container.util.Callables;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class UIInputInjectionDispirateTypesTest
{
   @Deployment
   @Dependencies(@Addon(name = "org.jboss.forge.addon:ui", version = "2.0.0-SNAPSHOT"))
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(AddonDependencyEntry.create(AddonId.from("org.jboss.forge.addon:ui", "2.0.0-SNAPSHOT")));

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
