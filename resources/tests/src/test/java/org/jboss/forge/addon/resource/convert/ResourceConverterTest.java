/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.resource.convert;

import java.io.File;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ResourceConverterTest
{
   @Deployment
   @Dependencies({ @AddonDependency(name = "org.jboss.forge.addon:convert"),
            @AddonDependency(name = "org.jboss.forge.addon:resources") })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addBeansXML()
               .addClass(ResourceConverterTest.class)
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:resources"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:convert"));

      return archive;
   }

   @Inject
   private Converter<File, DirectoryResource> resourceDirConverter;

   @Inject
   private ResourceFactory resourceFactory;
   
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
