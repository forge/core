package org.jboss.forge.dev.mvn;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import org.apache.maven.model.Model;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Before;
import org.junit.Test;

public class SetCommandsMavenPluginTest extends AbstractShellTest
{
   @Before
   public void copyFileForIDE() throws Exception
   {
      copyFile("maven-update/pom.xml");
      getShell().execute("cd target/test-classes/maven-update");
   }

   @SuppressWarnings("resource")
   private void copyFile(String fileName) throws IOException, FileNotFoundException
   {
      File source = new File("src/test/resources/", fileName);
      File target = new File("target/test-classes/", fileName);

      if (!target.exists())
      {
         target.getParentFile().mkdirs();
         target.createNewFile();
      }

      FileChannel input = new FileInputStream(source).getChannel();
      FileChannel output = new FileOutputStream(target).getChannel();

      output.transferFrom(input, 0, input.size());

      input.close();
      output.close();
   }

   @Test
   public void testShouldSetGroupId() throws Exception
   {
      getShell().execute("maven set-groupid \"groupId\"");
      
      String pom = getOutput();
      assertTrue(pom.contains("Set groupId [ groupId ]"));
      assertTrue(getModel().getGroupId().equals("groupId"));
   }

   @Test
   public void testShouldSetArtifactId() throws Exception
   {
      getShell().execute("maven set-artifactid \"artifactId\"");

      String pom = getOutput();
      assertTrue(pom.contains("Set artifactId [ artifactId ]"));
      assertTrue(getModel().getArtifactId().equals("artifactId"));
   }
   
   @Test
   public void testShouldSetVersion() throws Exception
   {
      getShell().execute("maven set-version \"1.0.0.Final\"");

      String pom = getOutput();
      assertTrue(pom.contains("Set version [ 1.0.0.Final ]"));
      assertTrue(getModel().getVersion().equals("1.0.0.Final"));
   }
   
   @Test
   public void testShouldSetName() throws Exception
   {
      getShell().execute("maven set-name \"BIZ-Layer\"");

      String pom = getOutput();
      assertTrue(pom.contains("Set name [ BIZ-Layer ]"));
      assertTrue(getModel().getName().equals("BIZ-Layer"));
   }

   private Model getModel()
   {
      MavenCoreFacet mvn = getProject().getFacet(MavenCoreFacet.class);
      return mvn.getPOM();
   }

}
