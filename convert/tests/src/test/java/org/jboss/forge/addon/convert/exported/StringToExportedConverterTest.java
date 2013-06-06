/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.convert.exported;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.arquillian.Addon;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class StringToExportedConverterTest
{
   @Deployment
   @Dependencies(@Addon(name = "org.jboss.forge.addon:convert", version = "2.0.0-SNAPSHOT"))
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addBeansXML()
               .addPackage(StringToExportedConverterTest.class.getPackage())
               .addAsAddonDependencies(
                        AddonDependencyEntry.create(AddonId.from("org.jboss.forge.addon:convert", "2.0.0-SNAPSHOT")));

      return archive;
   }

   @Inject
   private Converter<String, ExportedTestResult> converter;

   @Test
   public void testConverter() throws Exception
   {
      ExportedTestResult testResultOne = converter.convert("one");
      Assert.assertThat(testResultOne, is(instanceOf(TestResultOne.class)));

      ExportedTestResult testResultTwo = converter.convert("two");
      Assert.assertThat(testResultTwo, is(instanceOf(TestResultTwo.class)));
   }
}
