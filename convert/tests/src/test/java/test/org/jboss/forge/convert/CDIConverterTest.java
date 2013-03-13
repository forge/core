/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.org.jboss.forge.convert;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.Addon;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.container.addons.AddonDependency;
import org.jboss.forge.container.addons.AddonId;
import org.jboss.forge.convert.Converter;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class CDIConverterTest
{
   @Deployment
   @Dependencies(@Addon(name = "org.jboss.forge:convert", version = "2.0.0-SNAPSHOT"))
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addBeansXML()
               .addClass(CDIConverterTest.class)
               .addAsAddonDependencies(
                        AddonDependency.create(AddonId.from("org.jboss.forge:convert", "2.0.0-SNAPSHOT")));

      return archive;
   }

   @Inject
   private Converter<String, Long> stringToLong;

   @Inject
   private Converter<Integer, String> intToString;

   @Inject
   private Converter<Boolean, Boolean> noopConverter;

   @Test
   public void testNotNull() throws Exception
   {
      Assert.assertNotNull(stringToLong);
      Assert.assertNotNull(intToString);
   }

   @Test
   public void testSimpleConversion() throws Exception
   {
      String input = "123";
      Long expected = 123L;
      Assert.assertEquals(expected, stringToLong.convert(input));
   }

   @Test
   public void testSimpleConversion2() throws Exception
   {
      Integer input = 123;
      String expected = "123";
      Assert.assertEquals(expected, intToString.convert(input));
   }

   @Test
   public void testNoopConversion()
   {
      Boolean input = Boolean.TRUE;
      Boolean expected = input;
      Assert.assertSame(expected, noopConverter.convert(input));

   }

}
