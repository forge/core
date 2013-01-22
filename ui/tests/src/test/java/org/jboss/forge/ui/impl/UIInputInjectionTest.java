/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui.impl;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.Addon;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.container.AddonDependency;
import org.jboss.forge.container.AddonId;
import org.jboss.forge.ui.UIInput;
import org.jboss.forge.ui.UIInputCompleter;
import org.jboss.forge.ui.UIMetadata;
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
               .addAsAddonDependencies(AddonDependency.create(AddonId.from("org.jboss.forge:ui", "2.0.0-SNAPSHOT")));

      return archive;
   }

   @Inject
   UIInput<String> firstName;

   @Test
   public void testInjectionNotNull()
   {
      Assert.assertNotNull(firstName);
   }

   @Test
   public void testInputValues()
   {
      Assert.assertEquals("firstName", firstName.getName());
      Assert.assertEquals(String.class, firstName.getValueType());
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
      UIInputCompleter<String> originalCompleter = firstName.getCompleter();
      Assert.assertNotNull(originalCompleter);
      Assert.assertEquals(firstName, firstName.setCompleter(new UIInputCompleter<String>()
      {
         @Override
         public List<String> getCompletionProposals(String value)
         {
            return Arrays.asList("foo", "bar", "baz");
         }
      }));
      Assert.assertNotEquals(originalCompleter, firstName.getCompleter());
      Assert.assertEquals(3, firstName.getCompleter().getCompletionProposals(null).size());
      Assert.assertEquals(0, originalCompleter.getCompletionProposals(null).size());
   }

   @Test
   public void testMetadata()
   {
      UIMetadata metadata = firstName.getMetadata();
      Assert.assertNotNull(metadata);
      Assert.assertFalse(metadata.iterator().hasNext());

      metadata.set(UIInput.class, firstName);
      Assert.assertSame(firstName, firstName);
      Assert.assertSame(firstName, metadata.get(UIInput.class));
      Assert.assertNull(metadata.get(UIInputCompleter.class));
   }
}
