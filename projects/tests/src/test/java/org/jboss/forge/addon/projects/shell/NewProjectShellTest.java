/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.projects.shell;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.TimeUnit;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.shell.test.ShellTest;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.container.simple.Service;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.furnace.util.OperatingSystemUtils;
import org.jboss.forge.furnace.util.Streams;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class NewProjectShellTest
{
   @Deployment
   @AddonDependencies({
            @AddonDependency(name = "org.jboss.forge.furnace.container:simple"),
            @AddonDependency(name = "org.jboss.forge.addon:maven"),
            @AddonDependency(name = "org.jboss.forge.addon:shell-test-harness"),
            @AddonDependency(name = "org.jboss.forge.addon:projects")
   })
   public static AddonArchive getDeployment()
   {
      return ShrinkWrap
               .create(AddonArchive.class)
               .addAsServiceProvider(Service.class, NewProjectShellTest.class);
   }

   private ShellTest test;

   @Before
   public void setUp()
   {
      test = SimpleContainer.getServices(getClass().getClassLoader(), ShellTest.class).get();
   }

   @After
   public void tearDown() throws Exception
   {
      test.close();
   }

   @Test
   public void testWizardCommandExecution() throws Exception
   {
      File target = OperatingSystemUtils.createTempDir();

      Result result = test.execute(("project-new " +
               "--named lincoln " +
               "--top-level-package org.lincoln " +
               "--target-location " + target.getAbsolutePath() + " " +
               "--type jar " +
               "--overwrite " +
               "--version 1.0.0-SNAPSHOT"), 10, TimeUnit.SECONDS);

      Assert.assertFalse(result instanceof Failed);
      Assert.assertTrue(target.exists());
      Assert.assertTrue(target.isDirectory());
      File projectDir = new File(target, "lincoln");
      Assert.assertTrue(projectDir.exists());
      Assert.assertTrue(new File(projectDir, "pom.xml").exists());
   }

   @Test
   public void testTopLevelPackageOptional() throws Exception
   {
      File target = OperatingSystemUtils.createTempDir();

      Result result = test.execute(("project-new " +
               "--named lincoln-three " +
               "--target-location " + target.getAbsolutePath() + " " +
               "--type jar " +
               "--version 1.0.0-SNAPSHOT"), 10, TimeUnit.SECONDS);

      Assert.assertFalse(result instanceof Failed);
      Assert.assertTrue(target.exists());
      Assert.assertTrue(target.isDirectory());
      File projectDir = new File(target, "lincoln-three");
      Assert.assertTrue(projectDir.exists());

      File pomFile = new File(projectDir, "pom.xml");
      Assert.assertTrue(pomFile.exists());
      String pomContents = Streams.toString(new BufferedInputStream(
               new FileInputStream(pomFile)));
      Assert.assertTrue(pomContents.contains("org.lincoln.three"));
   }
}
