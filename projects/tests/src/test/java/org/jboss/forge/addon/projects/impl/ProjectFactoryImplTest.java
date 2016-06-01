/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.projects.impl;

/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.ProjectListener;
import org.jboss.forge.addon.projects.ProjectProvider;
import org.jboss.forge.addon.projects.facets.WebResourcesFacet;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.container.simple.Service;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.furnace.services.Imported;
import org.jboss.forge.furnace.spi.ListenerRegistration;
import org.jboss.forge.furnace.util.Predicate;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ProjectFactoryImplTest
{
   @Deployment
   @AddonDependencies({
            @AddonDependency(name = "org.jboss.forge.furnace.container:simple"),
            @AddonDependency(name = "org.jboss.forge.addon:maven"),
            @AddonDependency(name = "org.jboss.forge.addon:projects")
   })
   public static AddonArchive getDeployment()
   {
      return ShrinkWrap
               .create(AddonArchive.class)
               .addClass(MockProjectListener.class)
               .addAsServiceProvider(Service.class, ProjectFactoryImplTest.class,
                        MockProjectListener.class);
   }

   private ProjectFactory projectFactory;
   private Imported<ProjectProvider> buildSystems;

   @Before
   public void setUp()
   {
      projectFactory = SimpleContainer.getServices(getClass().getClassLoader(), ProjectFactory.class).get();
      buildSystems = SimpleContainer.getServices(getClass().getClassLoader(), ProjectProvider.class);
   }

   @Test
   public void testInjectionNotNull()
   {
      Assert.assertNotNull(projectFactory);
   }

   @Test
   public void testProjectListener() throws Exception
   {
      final AtomicBoolean projectSet = new AtomicBoolean(false);
      ListenerRegistration<ProjectListener> registration = projectFactory.addProjectListener(new ProjectListener()
      {
         @Override
         public void projectCreated(Project project)
         {
            projectSet.set(true);
         }
      });
      Assert.assertNotNull("Should not have returned a null listener registration", registration);
      Project project = projectFactory.createTempProject();
      registration.removeListener();
      Assert.assertNotNull(project);
      Assert.assertTrue("Listener was not called", projectSet.get());
   }

   @Test
   public void testFindProject() throws Exception
   {
      Project project = projectFactory.createTempProject();

      Assert.assertNotNull(project);
      Assert.assertNotNull(projectFactory.findProject(project.getRoot()));
      Assert.assertNull(projectFactory.findProject(project.getRoot(), new Predicate<Project>()
      {
         @Override
         public boolean accept(Project type)
         {
            return false;
         }
      }));

      Assert.assertNotNull(projectFactory
               .findProject(project.getRoot().reify(DirectoryResource.class).getChildDirectory("src/main/java")));

      project.getRoot().delete(true);
   }

   @Test
   public void testCreateTempProject()
   {
      Project project = projectFactory.createTempProject();
      Assert.assertNotNull(project);
      Assert.assertFalse(project.hasFacet(WebResourcesFacet.class));
   }

   @Test
   public void testCreateTempProjectWithBuildSystem() throws Exception
   {
      ProjectProvider buildSystem = buildSystems.get();
      try
      {
         Project project = projectFactory.createTempProject(buildSystem);
         Assert.assertNotNull(project);
      }
      finally
      {
         buildSystems.release(buildSystem);
      }
   }

   @Test
   public void testCreateTempProjectWithFacets()
   {
      Project project = projectFactory.createTempProject(Arrays
               .<Class<? extends ProjectFacet>> asList(WebResourcesFacet.class));
      Assert.assertNotNull(project);
      Assert.assertTrue(project.hasFacet(WebResourcesFacet.class));
   }

   @Test
   public void testCreateTempProjectWithBuildSystemAndFacets()
   {
      ProjectProvider buildSystem = buildSystems.get();
      try
      {
         Project project = projectFactory.createTempProject(buildSystem,
                  Arrays.<Class<? extends ProjectFacet>> asList(WebResourcesFacet.class));
         Assert.assertNotNull(project);
         Assert.assertTrue(project.hasFacet(WebResourcesFacet.class));
      }
      finally
      {
         buildSystems.release(buildSystem);
      }
   }

   @Test
   public void testContainsProject()
   {
      Project project = projectFactory.createTempProject();
      Assert.assertNotNull(project);
      Resource<?> projectRoot = project.getRoot();
      Assert.assertTrue(projectFactory.containsProject(projectRoot, projectRoot));
      Assert.assertTrue(projectFactory.containsProject(projectRoot, projectRoot.getChild("src")));

      projectRoot.delete(true);

      Assert.assertFalse(projectFactory.containsProject(projectRoot, projectRoot));
   }

   @Test
   public void testContainsProjectWithBuildSystem()
   {
      ProjectProvider buildSystem = buildSystems.get();
      try
      {
         Project project = projectFactory.createTempProject(buildSystem);
         Assert.assertNotNull(project);
         Resource<?> projectRoot = project.getRoot();
         Assert.assertTrue(projectFactory.containsProject(projectRoot, projectRoot, buildSystem));
         Assert.assertTrue(projectFactory.containsProject(projectRoot, projectRoot.getChild("src"),
                  buildSystem));

         projectRoot.delete(true);
         Assert.assertFalse(projectFactory.containsProject(projectRoot, projectRoot, buildSystem));
      }
      finally
      {
         buildSystems.release(buildSystem);
      }
   }

   @Test
   public void testProjectListenerExportedService()
   {
      Project project = projectFactory.createTempProject();
      Assert.assertNotNull(project);
      Assert.assertSame(project, MockProjectListener.project);
   }

}
