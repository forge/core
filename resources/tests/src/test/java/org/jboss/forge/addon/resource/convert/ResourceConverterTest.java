/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.resource.convert;

import java.io.File;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ResourceConverterTest
{
   private ConverterFactory converterFactory;
   private Converter<File, DirectoryResource> resourceDirConverter;
   private ResourceFactory resourceFactory;

   @Before
   public void setUp() throws Exception
   {
      Class<?> thisClass = getClass();
      this.converterFactory = SimpleContainer.getServices(thisClass.getClassLoader(), ConverterFactory.class).get();
      this.resourceDirConverter = converterFactory.getConverter(File.class, DirectoryResource.class);
      this.resourceFactory = SimpleContainer.getServices(thisClass.getClassLoader(), ResourceFactory.class).get();
   }

   @Test
   public void testNotNull() throws Exception
   {
      Assert.assertNotNull(resourceDirConverter);
   }

   @Test
   public void testSimpleConversion() throws Exception
   {
      File input = new File(System.getProperty("java.io.tmpdir"));
      DirectoryResource output = resourceDirConverter.convert(input);
      Assert.assertNotNull(output);
   }

   @Test
   public void testEmptyConversion() throws Exception
   {
      Assert.assertNull(resourceDirConverter.convert(null));
      Assert.assertNull(resourceFactory.create(""));
   }

}
