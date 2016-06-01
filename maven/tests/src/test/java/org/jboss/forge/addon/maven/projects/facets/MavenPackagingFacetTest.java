/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.projects.facets;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.building.ProjectBuilder;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
import org.jboss.forge.addon.projects.facets.PackagingFacet;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.container.simple.Service;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.furnace.util.Strings;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class MavenPackagingFacetTest
{
   @Deployment
   @AddonDependencies({
            @AddonDependency(name = "org.jboss.forge.addon:resources"),
            @AddonDependency(name = "org.jboss.forge.addon:projects"),
            @AddonDependency(name = "org.jboss.forge.addon:maven"),
            @AddonDependency(name = "org.jboss.forge.furnace.container:simple")
   })
   public static AddonArchive getDeployment()
   {
      AddonArchive archive = ShrinkWrap
               .create(AddonArchive.class)
               .addAsServiceProvider(Service.class, MavenPackagingFacetTest.class);

      return archive;
   }

   private Project project;
   private ProjectFactory projectFactory;

   @Before
   public void setUp()
   {
      projectFactory = SimpleContainer.getServices(getClass().getClassLoader(), ProjectFactory.class).get();
      project = projectFactory.createTempProject();
   }

   @Test
   public void testHasFacet() throws Exception
   {
      Assert.assertTrue("PackagingFacet not installed in project", project.hasFacet(PackagingFacet.class));
   }

   @Test
   public void testFinalName() throws Exception
   {
      final PackagingFacet facet = project.getFacet(PackagingFacet.class);
      Assert.assertNotNull("Final name is null", facet.getFinalName());
      MetadataFacet mFacet = project.getFacet(MetadataFacet.class);
      String finalName = mFacet.getProjectName() + "-" + mFacet.getProjectVersion();
      Assert.assertEquals(finalName, facet.getFinalName());
   }

   @Test
   public void testBuildArtifactResolved() throws Exception
   {
      final PackagingFacet facet = project.getFacet(PackagingFacet.class);
      Resource<?> finalArtifact = facet.getFinalArtifact();
      Assert.assertFalse("Final Artifact contains unresolved ${project.basedir} property", finalArtifact
               .getFullyQualifiedName().contains("${project.basedir}"));
   }

   @Test
   public void testBuildOutput() throws Exception
   {
      final PackagingFacet facet = project.getFacet(PackagingFacet.class);
      ProjectBuilder builder = facet.createBuilder();
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      ByteArrayOutputStream err = new ByteArrayOutputStream();

      builder.addArguments("clean").runTests(false)
               .build(new PrintStream(out, true), new PrintStream(err, true));
      Assert.assertThat(err.toString(), equalTo(Strings.EMPTY));
      Assert.assertThat(out.toString(), containsString("BUILD SUCCESS"));
   }

   @Test
   public void testBuildOutputQuiet() throws Exception
   {
      final PackagingFacet facet = project.getFacet(PackagingFacet.class);
      ProjectBuilder builder = facet.createBuilder();
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      ByteArrayOutputStream err = new ByteArrayOutputStream();

      builder.addArguments("clean", "--quiet").runTests(false)
               .build(new PrintStream(out, true), new PrintStream(err, true));
      Assert.assertThat(err.toString(), equalTo(Strings.EMPTY));
      Assert.assertThat(out.toString(), equalTo(Strings.EMPTY));
   }

   @Test
   public void testBuildOutputQuietInBuilder() throws Exception
   {
      final PackagingFacet facet = project.getFacet(PackagingFacet.class);
      ProjectBuilder builder = facet.createBuilder();
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      ByteArrayOutputStream err = new ByteArrayOutputStream();

      builder.addArguments("clean").runTests(false).quiet(true)
               .build(new PrintStream(out, true), new PrintStream(err, true));
      Assert.assertEquals(0, err.size());
      Assert.assertEquals(0, out.size());
   }

}
