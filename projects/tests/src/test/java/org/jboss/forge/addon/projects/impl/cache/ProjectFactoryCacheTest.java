package org.jboss.forge.addon.projects.impl.cache;

/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.impl.MockProjectListener;
import org.jboss.forge.addon.projects.spi.ProjectCache;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.furnace.services.Imported;
import org.jboss.forge.furnace.util.Predicate;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ProjectFactoryCacheTest
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
   private Imported<ProjectCache> caches;

   @Inject
   private ProjectFactory projectFactory;

   @Test
   public void testInjectionNotNull()
   {
      Assert.assertNotNull(caches);
   }

   @Test
   public void testFindProjectFromCache() throws Exception
   {
      Project project = projectFactory.createTempProject();

      Assert.assertNotNull(project);
      Project found = projectFactory.findProject(project.getProjectRoot());
      Assert.assertNotNull(found);
      Assert.assertSame(project, found);

      Assert.assertNull(projectFactory.findProject(project.getProjectRoot(), new Predicate<Project>()
      {
         @Override
         public boolean accept(Project type)
         {
            return false;
         }
      }));

      Project found2 = projectFactory.findProject(project.getProjectRoot().getChildDirectory("src/main/java"));
      Assert.assertNotNull(found2);
      Assert.assertSame(found, found2);

      Project project2 = projectFactory.createTempProject();
      Assert.assertNotSame(found2, project2);
      Assert.assertNotEquals(found2.getProjectRoot().getFullyQualifiedName(), project2.getProjectRoot()
               .getFullyQualifiedName());

      project.getProjectRoot().delete(true);
   }

}
