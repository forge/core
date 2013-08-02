/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.impl;

import java.util.Arrays;
import java.util.Iterator;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UIInputMany;
import org.jboss.forge.addon.ui.input.UISelectMany;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ManyValuesInjectionTest
{
   @Deployment
   @Dependencies({ @AddonDependency(name = "org.jboss.forge.addon:ui"),
            @AddonDependency(name = "org.jboss.forge.addon:convert"),
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi") })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addBeansXML()
               .addClasses(Career.class)
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.addon:convert"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui"),
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"));

      return archive;
   }

   @Inject
   UIInputMany<Career> inputMany;

   @Inject
   UISelectMany<Career> selectMany;

   @Inject
   AddonRegistry addonRegistry;

   @Test
   public void testInjectionNotNull()
   {
      Assert.assertNotNull(inputMany);
      Assert.assertNotNull(selectMany);
   }

   @Test
   @SuppressWarnings({ "rawtypes", "unchecked" })
   public void testIterableConversion()
   {
      Iterable<String> stringIterable = Arrays.asList("TECHNOLOGY", "MEDICINE");
      InputComponents.setValueFor(addonRegistry.getServices(ConverterFactory.class).get(),
               (InputComponent) inputMany, stringIterable);
      Iterable<Career> value = inputMany.getValue();
      Assert.assertNotNull(value);
      Iterator<Career> iterator = value.iterator();
      Assert.assertTrue(iterator.hasNext());
      Assert.assertEquals(Career.TECHNOLOGY, iterator.next());
      Assert.assertTrue(iterator.hasNext());
      Assert.assertEquals(Career.MEDICINE, iterator.next());
      Assert.assertFalse(iterator.hasNext());
   }
}