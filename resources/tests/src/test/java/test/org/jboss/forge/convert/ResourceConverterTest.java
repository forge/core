/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.org.jboss.forge.convert;

import java.io.File;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.Addon;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.container.AddonDependency;
import org.jboss.forge.container.AddonId;
import org.jboss.forge.convert.Converter;
import org.jboss.forge.resource.DirectoryResource;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ResourceConverterTest
{
   @Deployment
   @Dependencies({ @Addon(name = "org.jboss.forge:convert", version = "2.0.0-SNAPSHOT"),
            @Addon(name = "org.jboss.forge:resources", version = "2.0.0-SNAPSHOT") })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addAsManifestResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml"))
               .addClass(ResourceConverterTest.class)
               .addAsAddonDependencies(
                        AddonDependency.create(AddonId.from("org.jboss.forge:resources", "2.0.0-SNAPSHOT")),
                        AddonDependency.create(AddonId.from("org.jboss.forge:convert", "2.0.0-SNAPSHOT")));

      return archive;
   }

   @Inject
   private Converter<File, DirectoryResource> resourceDirConverter;

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
}
