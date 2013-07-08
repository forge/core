package org.jboss.forge.addon.projects.impl.facets;

/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@Ignore
@RunWith(Arquillian.class)
public class ProjectFacetConstraintsTest
{
   @Deployment
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.addon:resources", version = "2.0.0-SNAPSHOT"),
            @AddonDependency(name = "org.jboss.forge.addon:maven", version = "2.0.0-SNAPSHOT"),
            @AddonDependency(name = "org.jboss.forge.addon:projects", version = "2.0.0-SNAPSHOT")
   })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addClasses(ProjectFacetA.class, ProjectFacetB.class, ProjectFacetC.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.addon:projects", "2.0.0-SNAPSHOT")
               );

      return archive;
   }

   @Inject
   private ProjectFactory projectFactory;
   @Inject
   private FacetFactory facetFactory;

   @Test
   public void testInjectionNotNull()
   {
      Assert.assertNotNull(projectFactory);
   }

   @Test
   public void testFacetFactoryInstallationInstallsDependencies() throws Exception
   {
      Project project = projectFactory.createTempProject();
      facetFactory.install(project, ProjectFacetA.class);

      Assert.assertTrue(project.hasFacet(ProjectFacetA.class));
      Assert.assertTrue(project.hasFacet(ProjectFacetB.class));
      Assert.assertTrue(project.hasFacet(ProjectFacetC.class));
   }

   @Test
   public void testProjectFacetInstallationInstallsDependencies() throws Exception
   {
      Project project = projectFactory.createTempProject();
      facetFactory.install(project, ProjectFacetB.class);

      Assert.assertTrue(project.hasFacet(ProjectFacetB.class));
      Assert.assertTrue(project.hasFacet(ProjectFacetC.class));
   }

}
