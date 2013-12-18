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
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class UIInputDefaultValueTest
{
   @Deployment
   @Dependencies({ @AddonDependency(name = "org.jboss.forge.addon:ui"),
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi") })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addBeansXML()
               .addClasses(Career.class, Gender.class)
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui"),
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"));

      return archive;
   }

   @Inject
   @WithAttributes(defaultValue = "true", label = "")
   private UIInput<Boolean> booleanWithDefault;

   @Inject
   @WithAttributes(label = "")
   private UIInput<Boolean> booleanNoDefault;

   @Inject
   @WithAttributes(defaultValue = "true", label = "")
   private UIInput<String> stringWithDefault;

   @Inject
   @WithAttributes(label = "")
   private UIInput<String> stringNoDefault;

   @Test
   public void testBooleanInputWithDefaultValue()
   {
      Assert.assertTrue(booleanWithDefault.hasDefaultValue());
      Assert.assertFalse(booleanWithDefault.hasValue());
      Assert.assertTrue(booleanWithDefault.getValue());
   }

   @Test
   public void testBooleanInputNoDefaultValuePresetToFalse()
   {
      Assert.assertTrue(booleanNoDefault.hasDefaultValue());
      Assert.assertFalse(booleanNoDefault.hasValue());
      Assert.assertFalse(booleanNoDefault.getValue());
   }

   @Test
   public void testStringInputWithDefaultValue()
   {
      Assert.assertTrue(stringWithDefault.hasDefaultValue());
      Assert.assertFalse(stringWithDefault.hasValue());
      Assert.assertEquals("true", stringWithDefault.getValue());
   }

   @Test
   public void testStringInputNoDefaultValue()
   {
      Assert.assertFalse(stringNoDefault.hasDefaultValue());
      Assert.assertFalse(stringNoDefault.hasValue());
      Assert.assertNull(stringNoDefault.getValue());
   }
}
