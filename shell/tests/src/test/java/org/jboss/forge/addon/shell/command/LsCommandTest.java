/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.command;

import static org.hamcrest.CoreMatchers.instanceOf;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.shell.test.ShellTest;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.arquillian.AddonDeployment;
import org.jboss.forge.arquillian.AddonDeployments;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class LsCommandTest
{
   @Deployment
   @AddonDeployments({
            @AddonDeployment(name = "org.jboss.forge.addon:maven"),
            @AddonDeployment(name = "org.jboss.forge.addon:ui"),
            @AddonDeployment(name = "org.jboss.forge.addon:projects"),
            @AddonDeployment(name = "org.jboss.forge.addon:shell-test-harness"),
            @AddonDeployment(name = "org.jboss.forge.addon:resources"),
            @AddonDeployment(name = "org.jboss.forge.furnace.container:cdi")
   })
   public static AddonArchive getDeployment()
   {
      AddonArchive archive = ShrinkWrap
               .create(AddonArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.addon:maven"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:projects"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:shell-test-harness"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:resources"),
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"));

      return archive;
   }

   @Inject
   private ShellTest shellTest;

   @Inject
   private ProjectFactory projectFactory;

   @After
   public void tearDown() throws Exception
   {
      shellTest.close();
   }

   @Test
   public void testLsCommand() throws Exception
   {
      Project project = projectFactory.createTempProject();
      String projectPath = project.getRoot().getFullyQualifiedName();
      shellTest.execute("cd " + projectPath, 15, TimeUnit.SECONDS);
      shellTest.execute("touch file.txt", 15, TimeUnit.SECONDS);
      shellTest.clearScreen();
      shellTest.execute("ls *file*", 15, TimeUnit.SECONDS);
      Assert.assertThat(shellTest.getStdOut(), CoreMatchers.containsString("file.txt"));
   }

   @Test
   public void testLsCommandFailed() throws Exception
   {
      Project project = projectFactory.createTempProject();
      String projectPath = project.getRoot().getFullyQualifiedName();
      shellTest.execute("cd " + projectPath, 15, TimeUnit.SECONDS);
      shellTest.clearScreen();
      Result result = shellTest.execute(
               "ls foo" + File.separator + "jee-example-app-1.0.0.ear", 5,
               TimeUnit.SECONDS);
      Assert.assertThat(result, instanceOf(Failed.class));
      Assert.assertThat(result.getMessage(), CoreMatchers.containsString("No such file or directory"));
   }
}
