package org.jboss.forge.dev.zip;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.forge.project.services.ResourceFactory;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.resources.ZipEntryResource;
import org.jboss.forge.resources.ZipResource;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.test.AbstractShellTest;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ByteArrayAsset;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class LsZipPluginTest extends AbstractShellTest
{
   private static final String PKG = LsZipPluginTest.class.getSimpleName().toLowerCase();

   private static File JAR;

   @Inject
   private ResourceFactory factory;

   private int outputPartIndex;

   @Deployment
   public static JavaArchive getDeployment()
   {
      return AbstractShellTest.getDeployment().addPackages(true, LsZipPlugin.class.getPackage());
   }

   @BeforeClass
   public static void createJar() throws IOException
   {
      JavaArchive jar = ShrinkWrap.create(JavaArchive.class, "test.jar")
               .addClass(LsZipPlugin.class)
               .addClass(ZipResource.class)
               .addClass(ZipEntryResource.class)
               .addAsManifestResource(new ByteArrayAsset("<beans/>".getBytes()), ArchivePaths.create("beans.xml"));

      JAR = File.createTempFile(PKG, ".jar");

      jar.as(ZipExporter.class).exportTo(JAR, true);
   }

   @AfterClass
   public static void deleteJar()
   {
      JAR.delete();
   }

   /**
    * Test the result of command "ls *.class" with wildcard. The current resource must be a JAR.
    */
   @Test
   public void testLsCommand() throws Exception
   {
      Shell shell = getShell();

      prepareResource(shell);

      shell.execute("ls");

      assertOutput(
               "META-INF/",
               "META-INF/beans.xml",
               "org/",
               "org/jboss/",
               "org/jboss/forge/",
               "org/jboss/forge/dev/",
               "org/jboss/forge/dev/zip/",
               "org/jboss/forge/dev/zip/LsZipPlugin.class",
               "org/jboss/forge/resources/",
               "org/jboss/forge/resources/ZipEntryResource.class",
               "org/jboss/forge/resources/ZipResource.class");
   }

   /**
    * Test the result of command "ls". The current resource must be a JAR.
    */
   @Test
   public void testLsCommandWithWildcard() throws Exception
   {
      Shell shell = getShell();

      prepareResource(shell);

      shell.execute("ls *.class");

      assertOutput(
               "org/jboss/forge/dev/zip/LsZipPlugin.class",
               "org/jboss/forge/resources/ZipEntryResource.class",
               "org/jboss/forge/resources/ZipResource.class");
   }

   protected void prepareResource(Shell shell) throws Exception
   {
      shell.execute("cd " + JAR.getAbsolutePath());

      Resource<?> jarResource = factory.getResourceFrom(JAR);
      Resource<?> currentResource = getShell().getCurrentResource();

      System.out.println(jarResource.getFullyQualifiedName());
      System.out.println(currentResource.getFullyQualifiedName());

      Assert.assertEquals("Command CD dont target the JAR.", jarResource.getFullyQualifiedName(),
               currentResource.getFullyQualifiedName());

      // Save the index of output, for substring by command.
      outputPartIndex = getOutput().length();
   }

   protected void assertOutput(String... lines) throws IOException
   {
      List<String> linesList = Arrays.asList(lines);
      List<String> outputList = IOUtils.readLines(new StringReader(getOutput().substring(outputPartIndex)));

      for (String line : linesList)
      {
         Assert.assertTrue("Entry " + line + " is not in the output.", outputList.contains(line));
      }

      for (String line : outputList)
      {
         Assert.assertTrue("Output line " + line + " is not in archive.", linesList.contains(line));
      }
   }
}
