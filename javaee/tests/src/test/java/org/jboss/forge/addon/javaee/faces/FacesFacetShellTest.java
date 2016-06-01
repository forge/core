/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.util.OperatingSystemUtils;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.After;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
@Ignore("CI Server is freezing while running this test")
public class FacesFacetShellTest
{
   @Deployment
   @AddonDependencies({
            @AddonDependency(name = "org.jboss.forge.addon:shell-test-harness"),
            @AddonDependency(name = "org.jboss.forge.addon:javaee"),
            @AddonDependency(name = "org.jboss.forge.addon:maven"),
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi")
   })
   public static AddonArchive getDeployment()
   {
      return ShrinkWrap.create(AddonArchive.class).addBeansXML().addClass(ProjectHelper.class);
   }

   @Inject
   private ShellTest shellTest;

   @Inject
   private ProjectHelper projectHelper;

   @After
   public void tearDown() throws Exception
   {
      shellTest.close();
   }

   @Test
   public void testFacesFacetAvailability() throws Exception
   {
      Project project = projectHelper.createJavaLibraryProject();
      shellTest.getShell().setCurrentResource(project.getRoot());
      shellTest.execute("faces-setup --facesVersion 2.0", 15, TimeUnit.SECONDS);
      Assert.assertTrue(project.hasFacet(FacesFacet.class));
      Assert.assertTrue(project.hasFacet(FacesFacet_2_0.class));
   }

   @Test
   public void testFacesFacetAvailabilityThroughShell() throws Exception
   {
      Project project = projectHelper.createJavaLibraryProject();
      shellTest.getShell().setCurrentResource(project.getRoot());
      shellTest.execute("faces-setup --facesVersion 2.0", 15, TimeUnit.SECONDS);
      shellTest.execute("project-list-facets", 15, TimeUnit.SECONDS);
      Assert.assertThat(shellTest.getStdOut(), containsString("FacesFacet"));
   }

   @Test
   public void testFacesFacetAvailabilityThroughShellOnly() throws Exception
   {
      File tmpDir = OperatingSystemUtils.createTempDir();
      shellTest.execute("cd " + tmpDir.getAbsolutePath(), 15, TimeUnit.SECONDS);
      shellTest.execute("project-new --named project" + System.nanoTime(), 10, TimeUnit.SECONDS);
      shellTest.execute("faces-setup --facesVersion 2.0", 15, TimeUnit.SECONDS);
      shellTest.clearScreen();
      shellTest.execute("project-list-facets", 15, TimeUnit.SECONDS);
      Assert.assertThat(shellTest.getStdOut(), containsString("FacesFacet"));
   }
}
