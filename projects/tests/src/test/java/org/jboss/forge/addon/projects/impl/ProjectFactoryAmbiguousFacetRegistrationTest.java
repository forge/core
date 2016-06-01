/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.projects.impl;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.mock.MockAmbiguousProjectFacet;
import org.jboss.forge.addon.projects.mock.MockAmbiguousProjectFacet_1;
import org.jboss.forge.addon.projects.mock.MockAmbiguousProjectFacet_2;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.container.simple.Service;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ProjectFactoryAmbiguousFacetRegistrationTest
{
   @Deployment
   @AddonDependencies({
            @AddonDependency(name = "org.jboss.forge.furnace.container:simple"),
            @AddonDependency(name = "org.jboss.forge.addon:maven"),
            @AddonDependency(name = "org.jboss.forge.addon:projects")
   })
   public static AddonArchive getDeployment()
   {
      AddonArchive archive = ShrinkWrap
               .create(AddonArchive.class)
               .addClass(MockAmbiguousProjectFacet.class)
               .addClass(MockAmbiguousProjectFacet_1.class)
               .addClass(MockAmbiguousProjectFacet_2.class)
               .addAsServiceProvider(Service.class, ProjectFactoryAmbiguousFacetRegistrationTest.class,
                        MockAmbiguousProjectFacet_1.class,
                        MockAmbiguousProjectFacet_2.class);

      return archive;
   }

   private ProjectFactory projectFactory;

   @Before
   public void setUp()
   {
      projectFactory = SimpleContainer.getServices(getClass().getClassLoader(), ProjectFactory.class).get();
   }

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
      Project found = projectFactory.findProject(project.getRoot());
      Assert.assertNotNull(found);
      Assert.assertTrue(found.hasFacet(MockAmbiguousProjectFacet.class));

      project.getRoot().delete(true);
   }
}
