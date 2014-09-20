package org.jboss.forge.addon.projects.impl;

/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.maven.projects.MavenBuildSystem;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.dependencies.DependencyInstaller;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class DependencyInstallerImplTest
{
   @Deployment
   @Dependencies({
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
   private DependencyInstaller installer;

   @Inject
   private MavenBuildSystem buildSystem;

   private DirectoryResource projectDir;
   private Project project;

   @Before
   public void createProject() throws Exception
   {
      DirectoryResource addonDir = factory.create(forge.getRepositories().get(0).getRootDirectory()).reify(
               DirectoryResource.class);
      projectDir = addonDir.createTempResource();
      project = projectFactory.createProject(projectDir, buildSystem);
      MetadataFacet metadataFacet = project.getFacet(MetadataFacet.class);
      metadataFacet.setProjectName("test");
      metadataFacet.setProjectVersion("1.0");
      metadataFacet.setProjectGroupName("org.test");
      Assert.assertNotNull("Could not create test project", project);
   }

   @After
   public void destroyProject() throws Exception
   {
      projectDir.delete(true);
      project = null;
   }

   @Test
   public void testInstallDependency() throws Exception
   {
      DependencyFacet deps = project.getFacet(DependencyFacet.class);

      DependencyBuilder dependency = DependencyBuilder.create("org.jboss.forge.furnace:furnace-api");
      Assert.assertFalse(deps.hasEffectiveDependency(dependency));
      Assert.assertFalse(deps.hasEffectiveManagedDependency(dependency));
      installer.install(project, dependency);
      Assert.assertTrue(deps.hasDirectManagedDependency(dependency));
      Assert.assertTrue(deps.hasEffectiveDependency(dependency));
      Assert.assertTrue(deps.hasEffectiveManagedDependency(dependency));
      Assert.assertTrue(installer.isInstalled(project, dependency));
      Assert.assertTrue(installer.isManaged(project, dependency));

      Assert.assertNotNull(deps.getDirectManagedDependency(dependency).getCoordinate().getVersion());
      Assert.assertNull(deps.getDirectDependency(dependency).getCoordinate().getVersion());
   }
}
