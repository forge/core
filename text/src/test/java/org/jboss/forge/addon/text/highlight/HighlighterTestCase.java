package org.jboss.forge.addon.text.highlight;

import java.io.ByteArrayOutputStream;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.text.Highlighter;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class HighlighterTestCase {

   @Deployment
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.addon:text"),
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi") })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:text")
               ).addClass(TestScanner.class);

      return archive;
   }

   @Inject
   private Highlighter highlighter;

   @Test
   public void shouldFindImportedScannerServiceByType() {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      highlighter.byType(TestScanner.TYPE.getName(), "", out);

      Assert.assertTrue(out.toString().contains(TestScanner.TEST_STRING));
   }

   @Test
   public void shouldFindImportedScannerServiceByFileName() {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      highlighter.byFileName("something.test", "", out);

      Assert.assertTrue(out.toString().contains(TestScanner.TEST_STRING));
   }

   @Test
   public void shouldFindBuiltInScannerByType() {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      highlighter.byType("JAVA", "public void should()", out);

      Assert.assertTrue(out.toString().contains("public"));
   }

   @Test
   public void shouldFindBuiltInScannerByFileName() {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      highlighter.byFileName("test.java", "public void should()", out);

      Assert.assertTrue(out.toString().contains("public"));
   }

   @Test
   public void shouldFindScannerByAnyTypeCase() {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      highlighter.byType("JAva", "public void should()", out);

      Assert.assertTrue(out.toString().contains("public"));
   }

   @Test
   public void shouldFindScannerByAnyFileNameCase() {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      highlighter.byFileName("tAsE.JavA", "public void should()", out);

      Assert.assertTrue(out.toString().contains("public"));
   }
}