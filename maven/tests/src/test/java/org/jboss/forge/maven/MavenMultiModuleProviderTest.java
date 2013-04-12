package org.jboss.forge.maven;

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
import org.jboss.forge.arquillian.Addon;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.container.Forge;
import org.jboss.forge.container.addons.AddonId;
import org.jboss.forge.container.repositories.AddonDependencyEntry;
import org.jboss.forge.maven.projects.MavenFacet;
import org.jboss.forge.projects.Project;
import org.jboss.forge.projects.ProjectFactory;
import org.jboss.forge.projects.facets.MetadataFacet;
import org.jboss.forge.resource.DirectoryResource;
import org.jboss.forge.resource.ResourceFactory;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class MavenMultiModuleProviderTest
{
   @Deployment
   @Dependencies({
            @Addon(name = "org.jboss.forge:resources", version = "2.0.0-SNAPSHOT"),
            @Addon(name = "org.jboss.forge:projects", version = "2.0.0-SNAPSHOT"),
            @Addon(name = "org.jboss.forge:ui", version = "2.0.0-SNAPSHOT"),
            @Addon(name = "org.jboss.forge:maven", version = "2.0.0-SNAPSHOT")
   })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create(AddonId.from("org.jboss.forge:maven", "2.0.0-SNAPSHOT")),
                        AddonDependencyEntry.create(AddonId.from("org.jboss.forge:projects", "2.0.0-SNAPSHOT"))
               );

      return archive;
   }

   @Inject
   private ResourceFactory factory;

   @Inject
   private Forge forge;

   @Inject
   private ProjectFactory projectFactory;

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
      Project parentProject = projectFactory.createProject(projectDir);
      Assert.assertNotNull(parentProject);

      MetadataFacet metadata = parentProject.getFacet(MetadataFacet.class);
      metadata.setProjectName("parent");
      metadata.setTopLevelPackage("com.project.parent");

      DirectoryResource subProjectDir = parentProject.getProjectRoot().getChildDirectory("sub");
      Project subProject = projectFactory.createProject(subProjectDir);

      MavenFacet mavenFacet = parentProject.getFacet(MavenFacet.class);
      List<String> modules = mavenFacet.getPOM().getModules();
      Assert.assertTrue(!modules.isEmpty());
      Assert.assertEquals("sub", modules.get(0));
   }
}
