package org.jboss.forge.addon.projects.impl;

/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.ProjectListener;
import org.jboss.forge.addon.projects.facets.WebResourcesFacet;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.furnace.spi.ListenerRegistration;
import org.jboss.forge.furnace.util.Predicate;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ProjectFactoryImplTest
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
               .addClass(MockProjectListener.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:projects")
               );

      return archive;
   }

   @Inject
   private ProjectFactory projectFactory;

   @Test
   public void testInjectionNotNull()
   {
      Assert.assertNotNull(projectFactory);
   }

   @Test
   public void testCreateProject() throws Exception
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
      Assert.assertNotNull(projectFactory.findProject(project.getProjectRoot()));
      Assert.assertNull(projectFactory.findProject(project.getProjectRoot(), new Predicate<Project>()
      {
         @Override
         public boolean accept(Project type)
         {
            return false;
         }
      }));

      Assert.assertNotNull(projectFactory.findProject(project.getProjectRoot().getChildDirectory("src/main/java")));

      project.getProjectRoot().delete(true);
   }

   @Test
   public void testCreateTempProject()
   {
      Project project = projectFactory.createTempProject();
      Assert.assertNotNull(project);
      Assert.assertFalse(project.hasFacet(WebResourcesFacet.class));
   }

   @Test
   @SuppressWarnings("unchecked")
   public void testCreateTempProjectWithFacets()
   {
      Project project = projectFactory.createTempProject(Arrays
               .<Class<? extends ProjectFacet>> asList(WebResourcesFacet.class));
      Assert.assertNotNull(project);
      Assert.assertTrue(project.hasFacet(WebResourcesFacet.class));
   }

   @Test
   public void testContainsProject()
   {
      Project project = projectFactory.createTempProject();
      Assert.assertNotNull(project);
      DirectoryResource projectRoot = project.getProjectRoot();
      Assert.assertTrue(projectFactory.containsProject(projectRoot, projectRoot));
      Assert.assertTrue(projectFactory.containsProject(projectRoot, projectRoot.getChildDirectory("src")));

      projectRoot.delete(true);

      Assert.assertFalse(projectFactory.containsProject(projectRoot, projectRoot));
   }

   @Test
   public void testProjectListenerExportedService()
   {
      Project project = projectFactory.createTempProject();
      Assert.assertNotNull(project);
      Assert.assertSame(project, MockProjectListener.project);
   }

}
