/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.convert;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.AddonDeployment;
import org.jboss.forge.arquillian.AddonDeployments;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class CDIConverterTest
{
   @Deployment
   @AddonDeployments({
            @AddonDeployment(name = "org.jboss.forge.addon:convert"),
            @AddonDeployment(name = "org.jboss.forge.furnace.container:cdi")
   })
   public static AddonArchive getDeployment()
   {
      AddonArchive archive = ShrinkWrap
               .create(AddonArchive.class)
               .addBeansXML()
               .addClass(CDIConverterTest.class)
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:convert")
               );

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
