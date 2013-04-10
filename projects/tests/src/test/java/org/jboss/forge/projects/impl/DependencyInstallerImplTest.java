package org.jboss.forge.projects.impl;

/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.Addon;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.container.Forge;
import org.jboss.forge.container.addons.AddonId;
import org.jboss.forge.container.repositories.AddonDependencyEntry;
import org.jboss.forge.dependencies.builder.DependencyBuilder;
import org.jboss.forge.projects.Project;
import org.jboss.forge.projects.ProjectFactory;
import org.jboss.forge.projects.dependencies.DependencyInstaller;
import org.jboss.forge.projects.facets.DependencyFacet;
import org.jboss.forge.resource.DirectoryResource;
import org.jboss.forge.resource.ResourceFactory;
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

   @Inject
   private DependencyInstaller installer;

   // TODO refactor this into an AbstractProjectTest
   private DirectoryResource projectDir;
   private Project project;

   @Before
   public void createProject() throws Exception
   {
      DirectoryResource addonDir = factory.create(forge.getRepositories().get(0).getRootDirectory()).reify(
               DirectoryResource.class);
      projectDir = addonDir.createTempResource();
      project = projectFactory.createProject(projectDir);
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

      DependencyBuilder dependency = DependencyBuilder.create("org.jboss.forge:forge-addon-container-api");
      Assert.assertFalse(deps.hasEffectiveDependency(dependency));
      installer.install(project, dependency);
      Assert.assertTrue(deps.hasEffectiveDependency(dependency));
   }
}
