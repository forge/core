package org.jboss.forge.addon.maven;

/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

import java.io.File;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.maven.projects.MavenBuildSystemImpl;
import org.jboss.forge.addon.maven.projects.MavenFacet;
import org.jboss.forge.addon.maven.projects.MavenPluginFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
import org.jboss.forge.addon.projects.facets.PackagingFacet;
import org.jboss.forge.addon.projects.facets.ResourcesFacet;
import org.jboss.forge.addon.projects.facets.WebResourcesFacet;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.FileAsset;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class MavenBuildSystemImplTest
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
               .add(new FileAsset(new File("src/test/resources/pom-template.xml")),
                        "org/jboss/forge/addon/maven/pom-template.xml")
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:maven"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:projects")
               );

      return archive;
   }

   @Inject
   private ResourceFactory factory;

   @Inject
   private Furnace forge;

   @Inject
   private MavenBuildSystemImpl buildSystem;

   @Test
   public void testInjectionNotNull()
   {
      Assert.assertNotNull(buildSystem);
   }

   @Test
   public void testGetType()
   {
      Assert.assertEquals("Maven", buildSystem.getType());
   }

   @Test
   public void testProvidedFacets()
   {
      Assert.assertTrue(buildSystem.getProvidedFacetTypes().contains(MavenFacet.class));
      Assert.assertTrue(buildSystem.getProvidedFacetTypes().contains(MavenPluginFacet.class));
      Assert.assertTrue(buildSystem.getProvidedFacetTypes().contains(MetadataFacet.class));
      Assert.assertTrue(buildSystem.getProvidedFacetTypes().contains(PackagingFacet.class));
      Assert.assertTrue(buildSystem.getProvidedFacetTypes().contains(DependencyFacet.class));
      Assert.assertTrue(buildSystem.getProvidedFacetTypes().contains(ResourcesFacet.class));
   }

   @Test
   public void testFindProject() throws Exception
   {
      DirectoryResource addonDir = factory.create(forge.getRepositories().get(0).getRootDirectory()).reify(
               DirectoryResource.class);
      DirectoryResource projectDir = addonDir.createTempResource();
      FileResource<?> pomFile = projectDir.getChild("pom.xml").reify(FileResource.class);
      Assert.assertFalse(buildSystem.containsProject(projectDir));
      pomFile.createNewFile();
      pomFile.setContents(getClass().getResourceAsStream("pom-template.xml"));

      Assert.assertTrue(buildSystem.containsProject(projectDir));

      projectDir.delete(true);
   }

   @Test
   public void testEnabledFacets() throws Exception
   {
      DirectoryResource addonDir = factory.create(forge.getRepositories().get(0).getRootDirectory()).reify(
               DirectoryResource.class);
      DirectoryResource projectDir = addonDir.createTempResource();
      Project project = buildSystem.createProject(projectDir);
      boolean hasFacets = project.hasFacet(MavenFacet.class)
               && project.hasFacet(MavenPluginFacet.class)
               && project.hasFacet(MetadataFacet.class)
               && project.hasFacet(PackagingFacet.class)
               && project.hasFacet(DependencyFacet.class)
               && project.hasFacet(ResourcesFacet.class);
      Assert.assertTrue(hasFacets);

      projectDir.delete(true);
   }

}
