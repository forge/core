/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui.impl;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.Addon;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.ui.UI;
import org.jboss.forge.ui.UIInput;
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
   @Dependencies(@Addon(name = "org.jboss.forge:facets", version = "2.0.0-SNAPSHOT"))
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addPackages(true, UI.class.getPackage())
               .addAsManifestResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml"));

      System.out.println(archive);
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
      Assert.assertEquals(String.class, firstName.getType());
   }
}
