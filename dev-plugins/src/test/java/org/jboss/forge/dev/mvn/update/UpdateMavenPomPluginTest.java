/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.dev.mvn.update;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import org.jboss.forge.shell.Shell;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class UpdateMavenPomPluginTest extends AbstractShellTest
{

   @Before
   public void copyFileForIDE() throws Exception {

      copyFile("maven-update/pom.xml");
      copyFile("maven-update/sub/pom.xml");
   }

   @SuppressWarnings("resource")
   private void copyFile(String fileName) throws IOException, FileNotFoundException
   {
      File source = new File("src/test/resources/", fileName);
      File target = new File("target/test-classes/", fileName);

      if(!target.exists())
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
   public void testShouldBeAbleToLsPomFile() throws Exception
   {
      Shell shell = getShell();
      //shell.setAcceptDefaults(true);
      queueInputLines("", "2", "3", "", "", "", "", "", "");
      shell.execute("cd target/test-classes/maven-update");
      shell.execute("maven update");

      String pom = getOutput();

      Assert.assertTrue(
            "UpdatedDependency event should have been fired",
            pom.contains(UpdateEventObservers.UPDATED_TEXT));
      Assert.assertTrue(
            "UpdateingDependency event should been fired",
            pom.contains(UpdateEventObservers.VETO_TEXT));

      Assert.assertTrue(
            "Should allow to skip a given update",
            pom.contains("Skipping "));

      Assert.assertTrue(
            "Should show list of available versions",
            pom.contains("Which version would you like to update to"));

      System.out.println(pom);
   }
}
