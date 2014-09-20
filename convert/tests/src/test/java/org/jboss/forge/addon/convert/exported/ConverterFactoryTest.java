/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.convert.exported;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.convert.exception.ConverterNotFoundException;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class ConverterFactoryTest
{
   @Deployment
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.addon:convert"),
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi")
   })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addBeansXML()
               .addPackage(StringToExportedConverterTest.class.getPackage())
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:convert")
               );

      return archive;
   }

   @Inject
   private ConverterFactory converterFactory;

   @Test
   public void testConverterFactoryInjection()
   {
      Assert.assertNotNull(converterFactory);
   }

   @Test
   public void testPrimitiveConversion()
   {
      Converter<Boolean, Boolean> converter = converterFactory.getConverter(Boolean.class, boolean.class);
      Assert.assertEquals(Boolean.TRUE, converter.convert(Boolean.TRUE));
   }

   @Test
   public void testPrimitiveConversionFromString()
   {
      Converter<String, Boolean> converter = converterFactory.getConverter(String.class, boolean.class);
      Assert.assertEquals(Boolean.TRUE, converter.convert("true"));
   }

   @Test(expected = ConverterNotFoundException.class)
   public void testConverterNotFound()
   {
      converterFactory.getConverter(long.class, boolean.class);
   }

}
