/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.org.jboss.forge.ui.impl.converter;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.container.AddonDependency;
import org.jboss.forge.container.AddonId;
import org.jboss.forge.ui.UIInput;
import org.jboss.forge.ui.converter.Converter;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class CDIConverterTest
{
   @Deployment
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addAsManifestResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml"))
               .addAsAddonDependencies(AddonDependency.create(AddonId.from("org.jboss.forge:ui", "2.0.0-SNAPSHOT")));

      return archive;
   }

   @Deployment(testable = false, name = "org.jboss.forge:ui,2.0.0-SNAPSHOT")
   public static ForgeArchive getUIDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addPackages(true, UIInput.class.getPackage())
               .addAsManifestResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml"));

      return archive;
   }

   @Inject
   private Converter<String, Long> converter;

   @Test
   public void testNotNull() throws Exception
   {
      Assert.assertNotNull(converter);
   }

   @Test
   public void testSimpleConversion() throws Exception
   {
      String input = "123";
      Long expected = 123L;
      Assert.assertEquals(expected, converter.convert(input));
   }

}
