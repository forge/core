/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.testing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.addon.testing.facet.JUnitTestingFacet;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.container.simple.Service;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Ivan St. Ivanov
 */
@RunWith(Arquillian.class)
public class TestingFacetTest
{

   private Project project;
   private FacetFactory facetFactory;
   private JUnitTestingFacet jUnitTestingFacet;
   private DependencyFacet dependencyFacet;

   @Deployment
   @AddonDependencies
   public static AddonArchive getDeployment()
   {
      return ShrinkWrap.create(AddonArchive.class)
               .addAsServiceProvider(Service.class, TestingFacetTest.class);
   }

   @Before
   public void setUp()
   {
      ProjectFactory projectFactory = SimpleContainer.getServices(getClass().getClassLoader(), ProjectFactory.class)
               .get();
      facetFactory = SimpleContainer.getServices(getClass().getClassLoader(), FacetFactory.class).get();
      project = projectFactory.createTempProject();
      jUnitTestingFacet = SimpleContainer.getServices(getClass().getClassLoader(), JUnitTestingFacet.class).get();
      dependencyFacet = project.getFacet(DependencyFacet.class);
   }

   @Test
   public void testInstallJUnitFacetOnEmptyProject() throws Exception
   {
      final String frameworkVersion = "4.12";
      jUnitTestingFacet.setFrameworkVersion(frameworkVersion);
      facetFactory.install(project, jUnitTestingFacet);
      final DependencyBuilder junitDependency = getJUnitDependency();
      assertTrue(dependencyFacet.hasDirectDependency(junitDependency));
      assertEquals(frameworkVersion, dependencyFacet.getDirectDependency(junitDependency).getCoordinate().getVersion());
   }

   private DependencyBuilder getJUnitDependency()
   {
      return DependencyBuilder.create()
               .setGroupId("junit")
               .setArtifactId("junit")
               .setScopeType("test");
   }
}
