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
import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.ui.facets.HintsFacet;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UICompleter;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UIInputMany;
import org.jboss.forge.addon.ui.input.UISelectMany;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.arquillian.Addon;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.furnace.util.Callables;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class UIInputInjectionTest
{
   @Deployment
   @Dependencies(@Addon(name = "org.jboss.forge.addon:ui", version = "2.0.0-SNAPSHOT"))
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addBeansXML()
               .addClasses(Career.class, Gender.class)
               .addAsAddonDependencies(
                        AddonDependencyEntry.create(AddonId.from("org.jboss.forge.addon:ui", "2.0.0-SNAPSHOT")));

      return archive;
   }

   @Inject
   UIInput<String> firstName;

   @Inject
   UISelectOne<Career> careers;

   @Inject
   UISelectMany<String> partners;

   @Inject
   UIInputMany<String> unknown;

   @Inject
   @WithAttributes(label = "Attributed Input", enabled = false, required = false, requiredMessage = "REQUIRED_MESSAGE")
   UIInputMany<String> attributedInput;

   @Inject
   UISelectOne<Gender> gender;

   @Test
   public void testEnumValuesAvailability() throws Exception
   {
      Assert.assertNotNull(gender);
      Iterable<Gender> valueChoices = gender.getValueChoices();
      Iterator<Gender> it = valueChoices.iterator();
      Assert.assertNotNull(it);
      Assert.assertTrue(it.hasNext());
      Assert.assertEquals(Gender.MALE, it.next());
      Assert.assertTrue(it.hasNext());
      Assert.assertEquals(Gender.FEMALE, it.next());
      Assert.assertFalse(it.hasNext());
   }

   @Test
   public void testInjectionNotNull()
   {
      Assert.assertNotNull(firstName);
      Assert.assertNotNull(careers);
      Assert.assertNotNull(partners);
      Assert.assertNotNull(unknown);
      Assert.assertNotNull(gender);
   }

   @Test
   public void testInputValues()
   {
      Assert.assertEquals("firstName", firstName.getName());
      Assert.assertEquals(String.class, firstName.getValueType());

      Assert.assertEquals("careers", careers.getName());
      Assert.assertEquals(Career.class, careers.getValueType());

      Assert.assertEquals("partners", partners.getName());
      Assert.assertEquals(String.class, partners.getValueType());

      Assert.assertEquals("unknown", unknown.getName());
      Assert.assertEquals(String.class, unknown.getValueType());
   }

   @Test
   public void testValueConverters()
   {
      Converter<String, String> stringValueConverter = new Converter<String, String>()
      {
         @Override
         public String convert(String source)
         {
            return "NAME: " + source;
         }
      };

      Converter<String, Career> careersValueConverter = new Converter<String, Career>()
      {
         @Override
         public Career convert(String source)
         {
            return Career.valueOf(source);
         }
      };

      Assert.assertNull(firstName.getValueConverter());
      firstName.setValueConverter(stringValueConverter);
      Assert.assertSame(firstName.getValueConverter(), stringValueConverter);

      Assert.assertNull(careers.getValueConverter());
      careers.setValueConverter(careersValueConverter);
      Assert.assertSame(careers.getValueConverter(), careersValueConverter);

      Assert.assertNull(partners.getValueConverter());
      partners.setValueConverter(stringValueConverter);
      Assert.assertSame(partners.getValueConverter(), stringValueConverter);

      Assert.assertNull(unknown.getValueConverter());
      unknown.setValueConverter(stringValueConverter);
      Assert.assertSame(unknown.getValueConverter(), stringValueConverter);
   }

   @Test
   public void testRequired()
   {
      firstName.setRequired(true);
      Assert.assertTrue(firstName.isRequired());
      firstName.setRequired(false);
      Assert.assertFalse(firstName.isRequired());
   }

   @Test
   public void testDefaultValue()
   {
      String inputVal = "A String";
      firstName.setDefaultValue(inputVal);
      Assert.assertEquals(inputVal, firstName.getValue());
      final String inputVal2 = "Another String";

      firstName.setDefaultValue(Callables.returning(inputVal2));
      Assert.assertEquals(inputVal2, firstName.getValue());
   }

   @Test
   public void testCompleter()
   {
      UICompleter<String> originalCompleter = firstName.getCompleter();
      Assert.assertNotNull(originalCompleter);
      Assert.assertEquals(firstName, firstName.setCompleter(new UICompleter<String>()
      {
         @Override
         public Iterable<String> getCompletionProposals(InputComponent<?, String> input, String value)
         {
            return Arrays.asList("one", "two", "three");
         }
      }));
      Assert.assertNotEquals(originalCompleter, firstName.getCompleter());

      Iterator<String> iterator = firstName.getCompleter().getCompletionProposals(null, null).iterator();
      Assert.assertEquals("one", iterator.next());
      Assert.assertEquals("two", iterator.next());
      Assert.assertEquals("three", iterator.next());
      Assert.assertFalse(iterator.hasNext());

      Assert.assertFalse(originalCompleter.getCompletionProposals(null, null).iterator().hasNext());
   }

   @Test
   public void testInputType()
   {
      HintsFacet hints = firstName.getFacet(HintsFacet.class);
      InputType inputType = hints.getInputType();
      Assert.assertNull(inputType);

      hints.setInputType(InputType.TEXTAREA);
      Assert.assertSame(firstName, firstName);
      Assert.assertSame(InputType.TEXTAREA, hints.getInputType());
   }

   @Test
   public void testInputWithAttributes()
   {
      Assert.assertEquals("Attributed Input", attributedInput.getLabel());
      Assert.assertFalse(attributedInput.isEnabled());
      Assert.assertFalse(attributedInput.isRequired());
      Assert.assertEquals("REQUIRED_MESSAGE", attributedInput.getRequiredMessage());
   }
}
