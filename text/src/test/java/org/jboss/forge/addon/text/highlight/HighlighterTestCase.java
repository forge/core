/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.text.highlight;

import java.io.ByteArrayOutputStream;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.text.Highlighter;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.container.simple.Service;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class HighlighterTestCase
{
   @Deployment
   @AddonDependencies({
            @AddonDependency(name = "org.jboss.forge.addon:text"),
            @AddonDependency(name = "org.jboss.forge.furnace.container:simple")
   })
   public static AddonArchive getDeployment()
   {
      return ShrinkWrap.create(AddonArchive.class)
               .addClass(TestScanner.class)
               .addAsServiceProvider(Service.class, HighlighterTestCase.class, TestScanner.class);
   }

   private Highlighter highlighter;

   @Before
   public void setUp()
   {
      highlighter = SimpleContainer.getServices(getClass().getClassLoader(), Highlighter.class).get();
   }

   @Test
   public void shouldFindImportedScannerServiceByType()
   {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      highlighter.byType(TestScanner.TYPE.getName(), "", out);

      Assert.assertTrue(out.toString().contains(TestScanner.TEST_STRING));
   }

   @Test
   public void shouldFindImportedScannerServiceByFileName()
   {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      highlighter.byFileName("something.test", "", out);

      Assert.assertTrue(out.toString().contains(TestScanner.TEST_STRING));
   }

   @Test
   public void shouldFindBuiltInScannerByType()
   {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      highlighter.byType("JAVA", "public void should()", out);

      Assert.assertTrue(out.toString().contains("public"));
   }

   @Test
   public void shouldFindBuiltInScannerByFileName()
   {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      highlighter.byFileName("test.java", "public void should()", out);

      Assert.assertTrue(out.toString().contains("public"));
   }

   @Test
   public void shouldFindScannerByAnyTypeCase()
   {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      highlighter.byType("JAva", "public void should()", out);

      Assert.assertTrue(out.toString().contains("public"));
   }

   @Test
   public void shouldFindScannerByAnyFileNameCase()
   {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      highlighter.byFileName("tAsE.JavA", "public void should()", out);

      Assert.assertTrue(out.toString().contains("public"));
   }
}