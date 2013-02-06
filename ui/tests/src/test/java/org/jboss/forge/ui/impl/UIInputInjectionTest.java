/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui.impl;

import java.util.Arrays;
import java.util.Iterator;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.Addon;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.container.AddonDependency;
import org.jboss.forge.container.AddonId;
import org.jboss.forge.ui.UICompleter;
import org.jboss.forge.ui.UIInput;
import org.jboss.forge.ui.UIInputComponent;
import org.jboss.forge.ui.UIInputMany;
import org.jboss.forge.ui.UISelectMany;
import org.jboss.forge.ui.UISelectOne;
import org.jboss.forge.ui.facets.HintsFacet;
import org.jboss.forge.ui.hints.InputType;
import org.jboss.forge.ui.hints.InputTypes;
import org.jboss.forge.ui.util.Callables;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class UIInputInjectionTest
{
   @Deployment
   @Dependencies(@Addon(name = "org.jboss.forge:ui", version = "2.0.0-SNAPSHOT"))
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addAsManifestResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml"))
               .addClasses(Career.class)
               .addAsAddonDependencies(AddonDependency.create(AddonId.from("org.jboss.forge:ui", "2.0.0-SNAPSHOT")));

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

   @Test
   public void testInjectionNotNull()
   {
      Assert.assertNotNull(firstName);
      Assert.assertNotNull(careers);
      Assert.assertNotNull(partners);
      Assert.assertNotNull(unknown);
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
         public Iterable<String> getCompletionProposals(UIInputComponent<?, String> input, String value)
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

      hints.setInputType(InputTypes.TEXTAREA);
      Assert.assertSame(firstName, firstName);
      Assert.assertSame(InputTypes.TEXTAREA, hints.getInputType());
   }
}
