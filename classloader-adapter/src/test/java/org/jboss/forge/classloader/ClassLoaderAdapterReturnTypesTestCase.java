/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.classloader;

import javassist.Loader;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.Addon;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.container.AddonDependency;
import org.jboss.forge.container.AddonId;
import org.jboss.forge.container.AddonRegistry;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ClassLoaderAdapterReturnTypesTestCase
{
   @Deployment
   @Dependencies({ @Addon(name = "org.jboss.forge:ui-example", version = "2.0.0-SNAPSHOT") })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addClasses(ClassLoaderAdapterCallback.class)

               .addPackages(true, Loader.class.getPackage())
               .addAsManifestResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml"))
               .addAsAddonDependencies(
                        AddonDependency.create(AddonId.from("org.jboss.forge:ui", "2.0.0-SNAPSHOT")));

      return archive;
   }

   @Inject
   private AddonRegistry registry;

   @Test
   public void testClassLoaderAdapterGetRemoteService()
   {
      ClassLoader thisLoader = ClassLoaderAdapterReturnTypesTestCase.class.getClassLoader();
      ClassLoader uiLoader = null;

      for (org.jboss.forge.container.Addon addon : registry.getRegisteredAddons())
      {
         if ("org.jboss.forge:ui".equals(addon.getId().getName()))
            uiLoader = addon.getClassLoader();

      }

      AddonRegistry adapter = ClassLoaderAdapterCallback.enhance(uiLoader, thisLoader, registry);
      Assert.assertTrue(adapter.getClass().getName().contains("_javassist_"));
   }
}
