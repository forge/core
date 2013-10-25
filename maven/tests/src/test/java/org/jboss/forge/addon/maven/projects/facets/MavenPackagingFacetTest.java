/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.maven.projects.facets;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
import org.jboss.forge.addon.projects.facets.PackagingFacet;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class MavenPackagingFacetTest
{

   @Deployment
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.addon:resources"),
            @AddonDependency(name = "org.jboss.forge.addon:projects"),
            @AddonDependency(name = "org.jboss.forge.addon:maven")
   })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:maven"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:projects")
               );

      return archive;
   }

   private Project project;

   @Inject
   private ProjectFactory projectFactory;

   @Before
   public void setUp()
   {
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

}
