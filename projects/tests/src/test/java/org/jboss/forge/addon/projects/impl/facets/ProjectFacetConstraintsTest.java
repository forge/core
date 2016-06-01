/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.projects.impl.facets;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.mock.facets.ProjectFacetA;
import org.jboss.forge.addon.projects.mock.facets.ProjectFacetB;
import org.jboss.forge.addon.projects.mock.facets.ProjectFacetC;
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
public class ProjectFacetConstraintsTest
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
               .addClasses(ProjectFacetA.class, ProjectFacetB.class, ProjectFacetC.class)
               .addAsServiceProvider(Service.class, ProjectFacetConstraintsTest.class, ProjectFacetA.class,
                        ProjectFacetB.class, ProjectFacetC.class);
   }

   private FacetFactory facetFactory;
   private ProjectFactory projectFactory;

   @Before
   public void setUp()
   {
      projectFactory = SimpleContainer.getServices(getClass().getClassLoader(), ProjectFactory.class).get();
      facetFactory = SimpleContainer.getServices(getClass().getClassLoader(), FacetFactory.class).get();
   }

   @Test
   public void testFacetFactoryInstallationInstallsDependencies() throws Exception
   {
      Project project = projectFactory.createTempProject();
      facetFactory.install(project, ProjectFacetA.class);

      Assert.assertTrue(project.hasFacet(ProjectFacetA.class));
      Assert.assertTrue(project.hasFacet(ProjectFacetB.class));
      Assert.assertFalse(project.hasFacet(ProjectFacetC.class));
   }

   @Test
   public void testProjectFacetInstallationInstallsDependencies() throws Exception
   {
      Project project = projectFactory.createTempProject();
      facetFactory.install(project, ProjectFacetB.class);

      Assert.assertFalse(project.hasFacet(ProjectFacetA.class));
      Assert.assertTrue(project.hasFacet(ProjectFacetB.class));
      Assert.assertFalse(project.hasFacet(ProjectFacetC.class));
   }

}
