/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.projects.impl;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.dependencies.DependencyInstaller;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.container.simple.Service;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class DependencyInstallerImplTest
{
   private DependencyInstaller installer;
   private Project project;

   @Deployment
   @AddonDependencies({
            @AddonDependency(name = "org.jboss.forge.addon:resources"),
            @AddonDependency(name = "org.jboss.forge.addon:projects"),
            @AddonDependency(name = "org.jboss.forge.furnace.container:simple"),
            @AddonDependency(name = "org.jboss.forge.addon:maven")
   })
   public static AddonArchive getDeployment()
   {
      AddonArchive archive = ShrinkWrap
               .create(AddonArchive.class)
               .addAsServiceProvider(Service.class, DependencyInstallerImplTest.class);

      return archive;
   }

   @Before
   public void setUp()
   {
      installer = SimpleContainer.getServices(getClass().getClassLoader(), DependencyInstaller.class).get();
   }

   @Before
   public void createProject() throws Exception
   {
      ProjectFactory projectFactory = SimpleContainer.getServices(getClass().getClassLoader(), ProjectFactory.class)
               .get();
      project = projectFactory.createTempProject();
      MetadataFacet metadataFacet = project.getFacet(MetadataFacet.class);
      metadataFacet.setProjectName("test");
      metadataFacet.setProjectVersion("1.0");
      metadataFacet.setProjectGroupName("org.test");
      Assert.assertNotNull("Could not create test project", project);
   }

   @After
   public void destroyProject() throws Exception
   {
      project.getRoot().delete(true);
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
