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
import org.jboss.forge.addon.maven.projects.MavenFacet;
import org.jboss.forge.addon.maven.projects.MavenBuildSystemImpl;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
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
   private MavenBuildSystemImpl locator;

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

      MetadataFacet metadata = parentProject.getFacet(MetadataFacet.class);
      metadata.setProjectName("parent");
      metadata.setTopLevelPackage("com.project.parent");

      DirectoryResource subProjectDir = parentProject.getProjectRoot().getChildDirectory("sub");
      projectFactory.createProject(subProjectDir, locator);

      MavenFacet mavenFacet = parentProject.getFacet(MavenFacet.class);
      List<String> modules = mavenFacet.getPOM().getModules();
      Assert.assertTrue(!modules.isEmpty());
      Assert.assertEquals("sub", modules.get(0));
   }
}
