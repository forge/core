/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.addons.project;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.addons.facets.AddonAPIFacet;
import org.jboss.forge.addon.addons.facets.AddonImplFacet;
import org.jboss.forge.addon.addons.facets.AddonParentFacet;
import org.jboss.forge.addon.addons.facets.AddonSPIFacet;
import org.jboss.forge.addon.addons.facets.AddonTestFacet;
import org.jboss.forge.addon.addons.facets.DefaultFurnaceContainerAPIFacet;
import org.jboss.forge.addon.addons.facets.DefaultFurnaceContainerFacet;
import org.jboss.forge.addon.addons.facets.ForgeBOMFacet;
import org.jboss.forge.addon.addons.facets.FurnaceAPIFacet;
import org.jboss.forge.addon.addons.facets.FurnacePluginFacet;
import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.facets.FacetNotFoundException;
import org.jboss.forge.addon.javaee.cdi.CDIFacet;
import org.jboss.forge.addon.javaee.cdi.CDIFacet_1_1;
import org.jboss.forge.addon.maven.projects.MavenFacet;
import org.jboss.forge.addon.parser.java.facets.JavaCompilerFacet;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
import org.jboss.forge.addon.projects.facets.ResourcesFacet;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.furnace.versions.Version;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(Arquillian.class)
public class AddonProjectConfiguratorTest
{
   private static final String FORGE_ADDON_CLASSIFIER = "forge-addon";

   private AddonProjectConfigurator configurator;
   private ProjectFactory projectFactory;
   private Furnace furnace;

   @Before
   public void setUp()
   {
      projectFactory = SimpleContainer.getServices(getClass().getClassLoader(), ProjectFactory.class).get();
      configurator = SimpleContainer.getServices(getClass().getClassLoader(), AddonProjectConfigurator.class).get();
      furnace = SimpleContainer.getFurnace(getClass().getClassLoader());
   }

   @Test
   public void testComplexAddonProject() throws FileNotFoundException, FacetNotFoundException
   {
      Project project = projectFactory.createTempProject();
      project.getRoot().reify(DirectoryResource.class).deleteOnExit();

      MetadataFacet metadataFacet = project.getFacet(MetadataFacet.class);
      metadataFacet.setProjectName("testproject");
      metadataFacet.setProjectVersion("1.0.0-SNAPSHOT");
      metadataFacet.setProjectGroupName("com.acme.testproject");

      Version furnaceVersion = furnace.getVersion();
      configurator.setupComplexAddonProject(project, Collections.<AddonId>emptyList());
      assertTrue(project.hasFacet(AddonParentFacet.class));
      assertTrue(project.hasFacet(JavaCompilerFacet.class));
      assertFalse(project.hasFacet(JavaSourceFacet.class));
      assertFalse(project.hasFacet(CDIFacet.class));
      assertFalse(project.hasFacet(ResourcesFacet.class));
      Resource<?> projectRoot = project.getRoot();

      assertTrue("ADDON module is missing", projectRoot.getChild("addon").exists());
      assertTrue("API module is missing", projectRoot.getChild("api").exists());
      assertTrue("IMPL module is missing", projectRoot.getChild("impl").exists());
      assertTrue("SPI module is missing", projectRoot.getChild("spi").exists());
      assertTrue("TESTS module is missing", projectRoot.getChild("tests").exists());

      Project addonProject = projectFactory.findProject(projectRoot.getChild("addon"));
      Project apiProject = projectFactory.findProject(projectRoot.getChild("api"));
      Project implProject = projectFactory.findProject(projectRoot.getChild("impl"));
      Project spiProject = projectFactory.findProject(projectRoot.getChild("spi"));
      Project testsProject = projectFactory.findProject(projectRoot.getChild("tests"));

      assertTrue(project.hasFacet(ForgeBOMFacet.class));

      assertFalse(addonProject.hasFacet(ForgeBOMFacet.class));
      assertTrue(addonProject.hasFacet(JavaCompilerFacet.class));
      assertTrue(addonProject.hasFacet(JavaSourceFacet.class));
      assertTrue(addonProject.hasFacet(ResourcesFacet.class));
      assertFalse(addonProject.hasFacet(CDIFacet.class));

      assertFalse(apiProject.hasFacet(ForgeBOMFacet.class));
      assertTrue(apiProject.hasFacet(JavaCompilerFacet.class));
      assertTrue(apiProject.hasFacet(JavaSourceFacet.class));
      assertTrue(apiProject.hasFacet(ResourcesFacet.class));
      assertTrue(apiProject.hasFacet(CDIFacet_1_1.class));

      assertFalse(implProject.hasFacet(ForgeBOMFacet.class));
      assertTrue(implProject.hasFacet(JavaCompilerFacet.class));
      assertTrue(implProject.hasFacet(JavaSourceFacet.class));
      assertTrue(implProject.hasFacet(ResourcesFacet.class));
      assertTrue(implProject.hasFacet(CDIFacet_1_1.class));

      assertFalse(spiProject.hasFacet(ForgeBOMFacet.class));
      assertTrue(spiProject.hasFacet(JavaCompilerFacet.class));
      assertTrue(spiProject.hasFacet(JavaSourceFacet.class));
      assertTrue(spiProject.hasFacet(ResourcesFacet.class));
      assertTrue(spiProject.hasFacet(FurnaceAPIFacet.class));
      assertFalse(spiProject.hasFacet(CDIFacet_1_1.class));

      assertFalse(testsProject.hasFacet(ForgeBOMFacet.class));
      assertTrue(testsProject.hasFacet(JavaCompilerFacet.class));
      assertTrue(testsProject.hasFacet(JavaSourceFacet.class));
      assertTrue(testsProject.hasFacet(ResourcesFacet.class));
      assertFalse(testsProject.hasFacet(CDIFacet_1_1.class));

      Dependency addonDependency = DependencyBuilder.create(
               addonProject.getFacet(MetadataFacet.class).getOutputDependency())
               .setClassifier(FORGE_ADDON_CLASSIFIER);
      Dependency apiDependency = apiProject.getFacet(MetadataFacet.class).getOutputDependency();
      Dependency implDependency = implProject.getFacet(MetadataFacet.class).getOutputDependency();
      Dependency spiDependency = DependencyBuilder.create(
               spiProject.getFacet(MetadataFacet.class).getOutputDependency())
               .setClassifier(FORGE_ADDON_CLASSIFIER);

      /*
       * Verify parent project
       */
      assertNull(project.getFacet(MavenFacet.class).getModel().getParent());

      assertFalse(project.getRoot().getChild("src").exists());
      assertTrue(project.getFacet(DependencyFacet.class).hasDirectManagedDependency(
               DefaultFurnaceContainerAPIFacet.FURNACE_CONTAINER_API_DEPENDENCY));
      assertTrue(project.getFacet(DependencyFacet.class).hasDirectManagedDependency(
               DependencyBuilder.create(addonProject.getFacet(MetadataFacet.class).getOutputDependency())
                        .setClassifier(FORGE_ADDON_CLASSIFIER)));
      assertTrue(project.getFacet(DependencyFacet.class).hasDirectManagedDependency(apiDependency));
      assertTrue(project.getFacet(DependencyFacet.class).hasDirectManagedDependency(
               implProject.getFacet(MetadataFacet.class).getOutputDependency()));

      assertTrue(project.getFacet(DependencyFacet.class).hasDirectManagedDependency(spiDependency));

      /*
       * Verify impl/ sub-module
       */

      assertEquals(".." + File.separator + "pom.xml", implProject.getFacet(MavenFacet.class).getModel()
               .getParent().getRelativePath());
      assertTrue(implProject.hasFacet(DefaultFurnaceContainerAPIFacet.class));
      assertTrue(implProject.getFacet(JavaSourceFacet.class)
               .getJavaResource("com.acme.testproject.package-info.java").exists());

      assertTrue(implProject.getFacet(DependencyFacet.class).hasDirectDependency(apiDependency));
      assertFalse(implProject.getFacet(DependencyFacet.class).hasDirectManagedDependency(apiDependency));
      assertTrue(implProject.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(apiDependency));
      assertEquals("provided", implProject.getFacet(DependencyFacet.class).getDirectDependency(apiDependency)
               .getScopeType());

      assertTrue(implProject.getFacet(DependencyFacet.class).hasDirectDependency(spiDependency));
      assertFalse(implProject.getFacet(DependencyFacet.class).hasDirectManagedDependency(spiDependency));
      assertTrue(implProject.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(spiDependency));
      assertEquals("provided", implProject.getFacet(DependencyFacet.class).getDirectDependency(spiDependency)
               .getScopeType());

      // Assert.assertTrue(implProject.getFacet(DependencyFacet.class).getManagedDependencies().isEmpty());
      assertTrue(implProject.getFacet(DependencyFacet.class).hasDirectDependency(
               DefaultFurnaceContainerAPIFacet.FURNACE_CONTAINER_API_DEPENDENCY));
      assertFalse(implProject.getFacet(DependencyFacet.class).hasDirectManagedDependency(
               DefaultFurnaceContainerAPIFacet.FURNACE_CONTAINER_API_DEPENDENCY));
      assertTrue(implProject.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(
               DefaultFurnaceContainerAPIFacet.FURNACE_CONTAINER_API_DEPENDENCY));
      assertTrue(implProject.hasFacet(AddonImplFacet.class));
      assertFalse(implProject.hasFacet(FurnacePluginFacet.class));

      /*
       * Verify api/ sub-module
       */
      assertEquals(".." + File.separator + "pom.xml", apiProject.getFacet(MavenFacet.class).getModel()
               .getParent().getRelativePath());
      assertTrue(apiProject.hasFacet(DefaultFurnaceContainerAPIFacet.class));
      assertTrue(apiProject.getFacet(JavaSourceFacet.class)
               .getJavaResource("com.acme.testproject.package-info.java").exists());

      assertTrue(apiProject.getFacet(DependencyFacet.class).hasDirectDependency(spiDependency));
      assertFalse(apiProject.getFacet(DependencyFacet.class).hasDirectManagedDependency(spiDependency));
      assertTrue(apiProject.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(spiDependency));
      assertEquals("provided", apiProject.getFacet(DependencyFacet.class).getDirectDependency(spiDependency)
               .getScopeType());

      // Assert.assertTrue(apiProject.getFacet(DependencyFacet.class).getManagedDependencies().isEmpty());
      assertTrue(apiProject.getFacet(DependencyFacet.class).hasDirectDependency(
               DefaultFurnaceContainerAPIFacet.FURNACE_CONTAINER_API_DEPENDENCY));
      assertFalse(apiProject.getFacet(DependencyFacet.class).hasDirectManagedDependency(
               DefaultFurnaceContainerAPIFacet.FURNACE_CONTAINER_API_DEPENDENCY));
      assertTrue(apiProject.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(
               DefaultFurnaceContainerAPIFacet.FURNACE_CONTAINER_API_DEPENDENCY));
      assertTrue(apiProject.hasFacet(AddonAPIFacet.class));
      assertFalse(apiProject.hasFacet(FurnacePluginFacet.class));

      /*
       * Verify spi/ sub-module2.0.0.Final
       */
      assertEquals(".." + File.separator + "pom.xml", spiProject.getFacet(MavenFacet.class).getModel()
               .getParent().getRelativePath());
      assertTrue(spiProject.getFacet(JavaSourceFacet.class)
               .getJavaResource("com.acme.testproject.package-info.java").exists());

      // Assert.assertTrue(spiProject.getFacet(DependencyFacet.class).getManagedDependencies().isEmpty());
      assertTrue(spiProject.getFacet(DependencyFacet.class).hasDirectDependency(
               FurnaceAPIFacet.FURNACE_API_DEPENDENCY));
      assertFalse(spiProject.getFacet(DependencyFacet.class).hasDirectManagedDependency(
               FurnaceAPIFacet.FURNACE_API_DEPENDENCY));
      assertTrue(spiProject.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(
               FurnaceAPIFacet.FURNACE_API_DEPENDENCY));
      assertFalse(spiProject.hasFacet(DefaultFurnaceContainerFacet.class));
      assertFalse(spiProject.hasFacet(DefaultFurnaceContainerAPIFacet.class));
      assertFalse(spiProject.hasFacet(CDIFacet_1_1.class));
      assertTrue(spiProject.hasFacet(AddonSPIFacet.class));
      assertTrue(spiProject.hasFacet(FurnacePluginFacet.class));

      /*
       * Verify addon/ sub-module
       */
      assertEquals(".." + File.separator + "pom.xml", addonProject.getFacet(MavenFacet.class).getModel()
               .getParent()
               .getRelativePath());
      assertTrue(addonProject.getFacet(JavaSourceFacet.class)
               .getJavaResource("com.acme.testproject.package-info.java").exists());

      assertTrue(addonProject.getFacet(DependencyFacet.class).hasDirectDependency(apiDependency));
      assertFalse(addonProject.getFacet(DependencyFacet.class).hasDirectManagedDependency(apiDependency));
      assertTrue(addonProject.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(apiDependency));
      assertNull(addonProject.getFacet(DependencyFacet.class).getDirectDependency(apiDependency)
               .getScopeType());
      assertFalse(
               addonProject.getFacet(DependencyFacet.class).getDirectDependency(apiDependency).isOptional());

      assertTrue(addonProject.getFacet(DependencyFacet.class).hasDirectDependency(implDependency));
      assertFalse(addonProject.getFacet(DependencyFacet.class).hasDirectManagedDependency(implDependency));
      assertTrue(addonProject.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(implDependency));
      assertEquals("runtime", addonProject.getFacet(DependencyFacet.class).getDirectDependency(implDependency)
               .getScopeType());
      assertTrue(
               addonProject.getFacet(DependencyFacet.class).getDirectDependency(implDependency).isOptional());

      assertTrue(addonProject.getFacet(DependencyFacet.class).hasDirectDependency(spiDependency));
      assertFalse(addonProject.getFacet(DependencyFacet.class).hasDirectManagedDependency(spiDependency));
      assertTrue(addonProject.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(spiDependency));
      assertNull(addonProject.getFacet(DependencyFacet.class).getDirectDependency(spiDependency)
               .getScopeType());
      assertEquals("compile",
               addonProject.getFacet(DependencyFacet.class).getEffectiveDependency(spiDependency)
                        .getScopeType());
      assertFalse(
               addonProject.getFacet(DependencyFacet.class).getDirectDependency(spiDependency).isOptional());

      // Assert.assertTrue(addonProject.getFacet(DependencyFacet.class).getManagedDependencies().isEmpty());
      assertTrue(addonProject.getFacet(DependencyFacet.class).hasDirectDependency(
               DefaultFurnaceContainerFacet.FURNACE_CONTAINER_DEPENDENCY));
      assertFalse(addonProject.getFacet(DependencyFacet.class).hasDirectManagedDependency(
               DefaultFurnaceContainerFacet.FURNACE_CONTAINER_DEPENDENCY));
      assertTrue(addonProject.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(
               DefaultFurnaceContainerFacet.FURNACE_CONTAINER_DEPENDENCY));

      assertTrue(addonProject.hasFacet(FurnacePluginFacet.class));

      /*
       * Verify tests/ sub-module
       */

      assertEquals(".." + File.separator + "pom.xml", testsProject.getFacet(MavenFacet.class).getModel()
               .getParent()
               .getRelativePath());

      assertTrue(testsProject.getFacet(DependencyFacet.class).hasDirectDependency(addonDependency));
      assertFalse(testsProject.getFacet(DependencyFacet.class).hasDirectManagedDependency(addonDependency));
      assertTrue(testsProject.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(addonDependency));
      assertNull(testsProject.getFacet(DependencyFacet.class).getDirectDependency(addonDependency)
               .getScopeType());
      assertNotNull("ADDON module is not present in the TESTS module",
               testsProject.getFacet(DependencyFacet.class).getEffectiveDependency(addonDependency));
      assertEquals("compile",
               testsProject.getFacet(DependencyFacet.class).getEffectiveDependency(addonDependency)
                        .getScopeType());

      assertFalse(testsProject.getFacet(DependencyFacet.class).hasDirectDependency(spiDependency));
      assertFalse(testsProject.getFacet(DependencyFacet.class).hasDirectManagedDependency(spiDependency));
      assertTrue(testsProject.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(spiDependency));

      // Assert.assertTrue(testsProject.getFacet(DependencyFacet.class).getManagedDependencies().isEmpty());
      assertTrue(testsProject.getFacet(DependencyFacet.class).hasDirectDependency(
               DefaultFurnaceContainerFacet.FURNACE_CONTAINER_DEPENDENCY));
      assertFalse(testsProject.getFacet(DependencyFacet.class).hasDirectManagedDependency(
               DefaultFurnaceContainerFacet.FURNACE_CONTAINER_DEPENDENCY));
      assertTrue(testsProject.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(
               DefaultFurnaceContainerFacet.FURNACE_CONTAINER_DEPENDENCY));
      assertEquals(furnaceVersion.toString(),
               testsProject.getFacet(DependencyFacet.class).getEffectiveDependency(
                        DefaultFurnaceContainerFacet.FURNACE_CONTAINER_DEPENDENCY).getCoordinate()
                        .getVersion());

      assertTrue(project.getRoot().getChild("README.asciidoc").exists());
      project.getRoot().delete(true);
      project.getRoot().reify(DirectoryResource.class).deleteOnExit();
   }

   @Test
   public void testSimpleAddonProject() throws FileNotFoundException, FacetNotFoundException
   {
      Project project = projectFactory.createTempProject();
      project.getRoot().reify(DirectoryResource.class).deleteOnExit();
      MetadataFacet metadataFacet = project.getFacet(MetadataFacet.class);
      metadataFacet.setProjectName("testproject");
      metadataFacet.setProjectVersion("1.0.0-SNAPSHOT");
      metadataFacet.setProjectGroupName("com.acme.testproject");

      Version furnaceVersion = furnace.getVersion();
      configurator.setupSimpleAddonProject(project, Collections.<AddonId>emptyList());

      assertTrue(project.hasFacet(ForgeBOMFacet.class));
      assertTrue(project.hasFacet(DefaultFurnaceContainerFacet.class));
      assertTrue(project.hasFacet(FurnacePluginFacet.class));
      assertTrue(project.hasFacet(AddonTestFacet.class));
      assertTrue(project.hasFacet(JavaSourceFacet.class));
      assertTrue(project.hasFacet(JavaCompilerFacet.class));
      assertTrue(project.hasFacet(CDIFacet.class));
      assertTrue(project.hasFacet(CDIFacet_1_1.class));

      assertFalse(project.getFacet(DependencyFacet.class).getManagedDependencies().isEmpty());
      assertTrue(project.getFacet(JavaSourceFacet.class)
               .getJavaResource("com.acme.testproject.package-info.java").exists());

      /**
       * Verify test harness dependencies
       */
      assertTrue(project.getFacet(DependencyFacet.class).hasDirectDependency(
               AddonTestFacet.FURNACE_TEST_ADAPTER_DEPENDENCY));
      assertTrue(project.getFacet(DependencyFacet.class).hasDirectDependency(
               AddonTestFacet.FURNACE_TEST_HARNESS_DEPENDENCY));
      assertTrue(project.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(
               AddonTestFacet.FURNACE_TEST_ADAPTER_DEPENDENCY));
      assertTrue(project.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(
               AddonTestFacet.FURNACE_TEST_HARNESS_DEPENDENCY));
      assertTrue(project.getFacet(DependencyFacet.class).hasEffectiveDependency(
               DependencyBuilder.create("junit:junit").setScopeType("test")));

      /**
       * Verify container dependencies
       */
      assertTrue(project.getFacet(DependencyFacet.class).hasDirectDependency(
               DefaultFurnaceContainerFacet.FURNACE_CONTAINER_DEPENDENCY));
      assertNull(project.getFacet(DependencyFacet.class).getDirectDependency(
               DefaultFurnaceContainerFacet.FURNACE_CONTAINER_DEPENDENCY).getCoordinate().getVersion());
      assertTrue(project.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(
               DefaultFurnaceContainerFacet.FURNACE_CONTAINER_DEPENDENCY));
      assertNotNull(project.getFacet(DependencyFacet.class).getEffectiveManagedDependency(
               DefaultFurnaceContainerFacet.FURNACE_CONTAINER_DEPENDENCY).getCoordinate().getVersion());
      assertTrue(project.getFacet(DependencyFacet.class).hasEffectiveDependency(
               DefaultFurnaceContainerAPIFacet.FURNACE_CONTAINER_API_DEPENDENCY));
      assertEquals(furnaceVersion.toString(), project.getFacet(DependencyFacet.class).getEffectiveDependency(
               DefaultFurnaceContainerAPIFacet.FURNACE_CONTAINER_API_DEPENDENCY).getCoordinate().getVersion());
      assertFalse(project.getFacet(DependencyFacet.class).hasDirectDependency(
               DefaultFurnaceContainerAPIFacet.FURNACE_CONTAINER_API_DEPENDENCY));

      assertTrue(project.getRoot().getChild("README.asciidoc").exists());
      project.getRoot().delete(true);
   }
}
