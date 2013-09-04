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
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.mock.MockAmbiguousProjectFacet;
import org.jboss.forge.addon.projects.mock.MockAmbiguousProjectFacet_1;
import org.jboss.forge.addon.projects.mock.MockAmbiguousProjectFacet_2;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ProjectFactoryAmbiguousFacetRegistrationTest
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
               .addClass(MockAmbiguousProjectFacet.class)
               .addClass(MockAmbiguousProjectFacet_1.class)
               .addClass(MockAmbiguousProjectFacet_2.class)
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
   public void testCreateProject() throws Exception
   {
      Project project = projectFactory.createTempProject();
      Assert.assertNotNull(project);
      Assert.assertTrue(project.hasFacet(MockAmbiguousProjectFacet.class));
   }

   @Test
   public void testFindProject() throws Exception
   {
      Project project = projectFactory.createTempProject();

      Assert.assertNotNull(project);
      Project found = projectFactory.findProject(project.getProjectRoot());
      Assert.assertNotNull(found);
      Assert.assertTrue(found.hasFacet(MockAmbiguousProjectFacet.class));

      project.getProjectRoot().delete(true);
   }
}
