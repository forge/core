/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven;

/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

import java.io.File;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.maven.projects.MavenBuildSystem;
import org.jboss.forge.addon.maven.projects.MavenFacet;
import org.jboss.forge.addon.maven.projects.MavenPluginFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
import org.jboss.forge.addon.projects.facets.PackagingFacet;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.container.simple.Service;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.furnace.util.Iterators;
import org.jboss.forge.furnace.util.OperatingSystemUtils;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.FileAsset;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class MavenBuildSystemImplTest
{
   @Deployment
   @AddonDependencies({
            @AddonDependency(name = "org.jboss.forge.addon:resources"),
            @AddonDependency(name = "org.jboss.forge.addon:projects"),
            @AddonDependency(name = "org.jboss.forge.addon:maven"),
            @AddonDependency(name = "org.jboss.forge.furnace.container:simple")
   })
   public static AddonArchive getDeployment()
   {
      AddonArchive archive = ShrinkWrap
               .create(AddonArchive.class)
               .add(new FileAsset(new File("src/test/resources/pom-template.xml")),
                        "org/jboss/forge/addon/maven/pom-template.xml")
               .addAsServiceProvider(Service.class, MavenBuildSystemImplTest.class);

      return archive;
   }

   private ResourceFactory factory;
   private MavenBuildSystem buildSystem;

   @Before
   public void setUp()
   {
      factory = SimpleContainer.getServices(getClass().getClassLoader(), ResourceFactory.class).get();
      buildSystem = SimpleContainer.getServices(getClass().getClassLoader(), MavenBuildSystem.class).get();
   }

   @Test
   public void testInjectionNotNull()
   {
      Assert.assertNotNull(buildSystem);
   }

   @Test
   public void testGetType()
   {
      Assert.assertEquals("Maven", buildSystem.getType());
   }

   @Test
   public void testProvidedFacets()
   {
      Assert.assertTrue(Iterators.asList(buildSystem.getProvidedFacetTypes()).contains(MavenFacet.class));
      Assert.assertTrue(Iterators.asList(buildSystem.getProvidedFacetTypes()).contains(MavenPluginFacet.class));
      Assert.assertTrue(Iterators.asList(buildSystem.getProvidedFacetTypes()).contains(MetadataFacet.class));
      Assert.assertTrue(Iterators.asList(buildSystem.getProvidedFacetTypes()).contains(PackagingFacet.class));
      Assert.assertTrue(Iterators.asList(buildSystem.getProvidedFacetTypes()).contains(DependencyFacet.class));
   }

   @Test
   public void testFindProject() throws Exception
   {
      DirectoryResource projectDir = factory.create(OperatingSystemUtils.createTempDir())
               .reify(DirectoryResource.class);
      FileResource<?> pomFile = projectDir.getChild("pom.xml").reify(FileResource.class);
      Assert.assertFalse(buildSystem.containsProject(projectDir));
      pomFile.createNewFile();
      pomFile.setContents(getClass().getResourceAsStream("pom-template.xml"));

      Assert.assertTrue(buildSystem.containsProject(projectDir));

      projectDir.delete(true);
   }

   @Test
   public void testEnabledFacets() throws Exception
   {
      DirectoryResource projectDir = factory.create(OperatingSystemUtils.createTempDir())
               .reify(DirectoryResource.class);
      Project project = buildSystem.createProject(projectDir);
      boolean hasFacets = project.hasFacet(MavenFacet.class)
               && project.hasFacet(MavenPluginFacet.class)
               && project.hasFacet(MetadataFacet.class)
               && project.hasFacet(PackagingFacet.class)
               && project.hasFacet(DependencyFacet.class);
      Assert.assertTrue(hasFacets);

      projectDir.delete(true);
   }

}
