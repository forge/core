package org.jboss.forge.addon.maven;

/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.maven.projects.MavenBuildSystem;
import org.jboss.forge.addon.maven.projects.MavenFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
import org.jboss.forge.addon.projects.facets.PackagingFacet;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class MavenMultiModuleProviderTest
{
   @Deployment
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi"),
            @AddonDependency(name = "org.jboss.forge.addon:resources"),
            @AddonDependency(name = "org.jboss.forge.addon:projects"),
            @AddonDependency(name = "org.jboss.forge.addon:ui"),
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

   @Inject
   private ResourceFactory factory;

   @Inject
   private Furnace forge;

   @Inject
   private ProjectFactory projectFactory;

   @Inject
   private MavenBuildSystem locator;

   @Test
   public void testInjectionNotNull()
   {
      Assert.assertNotNull(projectFactory);
   }

   @Test
   public void testCreateNestedProject() throws Exception
   {
      DirectoryResource addonDir = factory.create(forge.getRepositories().get(0).getRootDirectory()).reify(
               DirectoryResource.class);
      DirectoryResource projectDir = addonDir.createTempResource();
      Project parentProject = projectFactory.createProject(projectDir, locator);
      Assert.assertNotNull(parentProject);
      
      parentProject.getFacet(PackagingFacet.class).setPackagingType("pom");
      
      MetadataFacet metadata = parentProject.getFacet(MetadataFacet.class);
      metadata.setProjectName("parent");
      metadata.setProjectGroupName("com.project.parent");

      DirectoryResource subProjectDir = parentProject.getRoot().reify(DirectoryResource.class).getChildDirectory("sub");
      projectFactory.createProject(subProjectDir, locator);

      MavenFacet mavenFacet = parentProject.getFacet(MavenFacet.class);
      List<String> modules = mavenFacet.getModel().getModules();
      Assert.assertFalse(modules.isEmpty());
      Assert.assertEquals("sub", modules.get(0));
   }

   @Test
   public void testCreateNestedProjectWithParentThatHasInheritedVersion() throws Exception
   {
      DirectoryResource addonDir = factory.create(forge.getRepositories().get(0).getRootDirectory()).reify(
               DirectoryResource.class);
      DirectoryResource projectDir = addonDir.createTempResource();
      Project parentProject = projectFactory.createProject(projectDir, locator);
      Assert.assertNotNull(parentProject);

      MetadataFacet metadata = parentProject.getFacet(MetadataFacet.class);
      metadata.setProjectName("parent");
      metadata.setProjectGroupName("com.project.parent");
      parentProject.getFacet(PackagingFacet.class).setPackagingType("pom");

      DirectoryResource intermediateProjectDir = parentProject.getRoot().reify(DirectoryResource.class)
               .getChildDirectory("intermediate");
      Project intermediateProject = projectFactory.createProject(intermediateProjectDir, locator);

      MavenFacet parentMavenFacet = parentProject.getFacet(MavenFacet.class);
      List<String> modules = parentMavenFacet.getModel().getModules();
      Assert.assertFalse(modules.isEmpty());
      Assert.assertEquals("intermediate", modules.get(0));

      intermediateProject.getFacet(MetadataFacet.class).setProjectVersion("");
      intermediateProject.getFacet(PackagingFacet.class).setPackagingType("pom");

      DirectoryResource subProjectDir = intermediateProject.getRoot().reify(DirectoryResource.class)
               .getChildDirectory("sub");
      Project subProject = projectFactory.createProject(subProjectDir, locator);

      MavenFacet intermediateMavenFacet = intermediateProject.getFacet(MavenFacet.class);
      List<String> intermediateModules = intermediateMavenFacet.getModel().getModules();
      Assert.assertFalse(intermediateModules.isEmpty());
      Assert.assertEquals("sub", intermediateModules.get(0));

      String version = subProject.getFacet(MetadataFacet.class).getProjectVersion();
      Assert.assertEquals(parentProject.getFacet(MetadataFacet.class).getProjectVersion(), version);

   }
}
