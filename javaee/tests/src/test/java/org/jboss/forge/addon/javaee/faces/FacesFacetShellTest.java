/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.faces;

import static org.hamcrest.CoreMatchers.containsString;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.javaee.ProjectHelper;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.shell.test.ShellTest;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.furnace.util.OperatingSystemUtils;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class FacesFacetShellTest
{
   @Deployment
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.addon:shell-test-harness"),
            @AddonDependency(name = "org.jboss.forge.addon:javaee"),
            @AddonDependency(name = "org.jboss.forge.addon:projects"),
            @AddonDependency(name = "org.jboss.forge.addon:maven")
   })
   public static ForgeArchive getDeployment()
   {
      return ShrinkWrap.create(ForgeArchive.class)
               .addBeansXML()
               .addClass(ProjectHelper.class)
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:projects"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:shell-test-harness"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:javaee")
               );
   }

   @Inject
   private ShellTest shell;

   @Inject
   private ProjectHelper projectHelper;

   @Before
   public void clearScreen() throws Exception
   {
      shell.clearScreen();
   }

   @Test
   public void testFacesFacetAvailability() throws Exception
   {
      Project project = projectHelper.createJavaLibraryProject();
      shell.getShell().setCurrentResource(project.getRoot());
      shell.execute("faces-setup --facesVersion 2.0", 5, TimeUnit.SECONDS);
      Assert.assertTrue(project.hasFacet(FacesFacet.class));
      Assert.assertTrue(project.hasFacet(FacesFacet_2_0.class));
   }

   @Test
   public void testFacesFacetAvailabilityThroughShell() throws Exception
   {
      Project project = projectHelper.createJavaLibraryProject();
      shell.getShell().setCurrentResource(project.getRoot());
      shell.execute("faces-setup --facesVersion 2.0", 5, TimeUnit.SECONDS);
      shell.execute("project-list-facets", 5, TimeUnit.SECONDS);
      Assert.assertThat(shell.getStdOut(), containsString("FacesFacet"));
   }

   @Test
   public void testFacesFacetAvailabilityThroughShellOnly() throws Exception
   {
      File tmpDir = OperatingSystemUtils.createTempDir();
      shell.execute("cd " + tmpDir.getAbsolutePath(), 5, TimeUnit.SECONDS);
      shell.execute("project-new --named project" + System.nanoTime(), 10, TimeUnit.SECONDS);
      shell.execute("faces-setup --facesVersion 2.0", 5, TimeUnit.SECONDS);
      clearScreen();
      shell.execute("project-list-facets", 5, TimeUnit.SECONDS);
      Assert.assertThat(shell.getStdOut(), containsString("FacesFacet"));
   }
}
