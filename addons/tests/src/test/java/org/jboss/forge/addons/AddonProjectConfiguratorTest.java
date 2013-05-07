/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addons;

import java.util.Collections;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addons.facets.ForgeContainerAPIFacet;
import org.jboss.forge.arquillian.Addon;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.container.Forge;
import org.jboss.forge.container.addons.AddonId;
import org.jboss.forge.container.repositories.AddonDependencyEntry;
import org.jboss.forge.container.versions.SingleVersion;
import org.jboss.forge.dependencies.Dependency;
import org.jboss.forge.dependencies.builder.DependencyBuilder;
import org.jboss.forge.maven.projects.MavenFacet;
import org.jboss.forge.projects.Project;
import org.jboss.forge.projects.ProjectFactory;
import org.jboss.forge.projects.facets.DependencyFacet;
import org.jboss.forge.projects.facets.MetadataFacet;
import org.jboss.forge.resource.DirectoryResource;
import org.jboss.forge.resource.ResourceFactory;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class AddonProjectConfiguratorTest
{
   @Deployment
   @Dependencies({
            @Addon(name = "org.jboss.forge:projects", version = "2.0.0-SNAPSHOT"),
            @Addon(name = "org.jboss.forge:maven", version = "2.0.0-SNAPSHOT")
   })
   public static ForgeArchive getDeployment()
   {
      return ShrinkWrap.create(ForgeArchive.class).
               addBeansXML().
               addPackages(true, AddonProjectConfigurator.class.getPackage()).
               addAsAddonDependencies(
                        AddonDependencyEntry.create(AddonId.from("org.jboss.forge:maven", "2.0.0-SNAPSHOT")),
                        AddonDependencyEntry.create(AddonId.from("org.jboss.forge:projects", "2.0.0-SNAPSHOT"))
               );
   }

   @Inject
   private AddonProjectConfigurator addonProjectFactory;

   @Inject
   private ProjectFactory projectFactory;

   @Inject
   private ResourceFactory resourceFactory;

   @Inject
   private Forge forge;

   @Test
   public void testCreateAddonProject()
   {
      DirectoryResource addonDir = resourceFactory.create(forge.getRepositories().get(0).getRootDirectory()).reify(
               DirectoryResource.class);
      DirectoryResource projectDir = addonDir.createTempResource();
      Project project = projectFactory.createProject(projectDir);
      SingleVersion forgeVersion = new SingleVersion("2.0.0.Alpha3");
      addonProjectFactory.setupAddonProject(project, forgeVersion, Collections.<AddonId> emptyList());

      DirectoryResource projectRoot = project.getProjectRoot();

      Project addonProject = projectFactory.findProject(projectRoot.getChildDirectory("addon"));
      Project apiProject = projectFactory.findProject(projectRoot.getChildDirectory("api"));
      Project implProject = projectFactory.findProject(projectRoot.getChildDirectory("impl"));
      Project spiProject = projectFactory.findProject(projectRoot.getChildDirectory("spi"));
      Project testsProject = projectFactory.findProject(projectRoot.getChildDirectory("tests"));

      Assert.assertTrue("ADDON module is missing", projectRoot.getChild("addon").exists());
      Assert.assertTrue("API module is missing", projectRoot.getChild("api").exists());
      Assert.assertTrue("IMPL module is missing", projectRoot.getChild("impl").exists());
      Assert.assertTrue("SPI module is missing", projectRoot.getChild("spi").exists());
      Assert.assertTrue("TESTS module is missing", projectRoot.getChild("tests").exists());

      Dependency addonDependency = DependencyBuilder.create(
               addonProject.getFacet(MetadataFacet.class).getOutputDependency())
               .setClassifier("forge-addon");
      Dependency apiDependency = apiProject.getFacet(MetadataFacet.class).getOutputDependency();
      Dependency implDependency = implProject.getFacet(MetadataFacet.class).getOutputDependency();
      Dependency spiDependency = DependencyBuilder.create(
               spiProject.getFacet(MetadataFacet.class).getOutputDependency())
               .setClassifier("forge-addon");

      /*
       * Verify parent project
       */
      Assert.assertNull(project.getFacet(MavenFacet.class).getPOM().getParent());

      Assert.assertTrue(project.getFacet(DependencyFacet.class).hasDirectManagedDependency(
               ForgeContainerAPIFacet.FORGE_API_DEPENDENCY));
      Assert.assertTrue(project.getFacet(DependencyFacet.class).hasDirectManagedDependency(
               DependencyBuilder.create(addonProject.getFacet(MetadataFacet.class).getOutputDependency())
                        .setClassifier("forge-addon")));
      Assert.assertTrue(project.getFacet(DependencyFacet.class).hasDirectManagedDependency(apiDependency));
      Assert.assertTrue(project.getFacet(DependencyFacet.class).hasDirectManagedDependency(
               implProject.getFacet(MetadataFacet.class).getOutputDependency()));

      Assert.assertTrue(project.getFacet(DependencyFacet.class).hasDirectManagedDependency(spiDependency));

      /*
       * Verify impl/ sub-module
       */
      Assert.assertEquals("../pom.xml", implProject.getFacet(MavenFacet.class).getPOM().getParent().getRelativePath());

      Assert.assertTrue(implProject.getFacet(DependencyFacet.class).hasDirectDependency(apiDependency));
      Assert.assertFalse(implProject.getFacet(DependencyFacet.class).hasDirectManagedDependency(apiDependency));
      Assert.assertTrue(implProject.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(apiDependency));
      Assert.assertEquals("provided", implProject.getFacet(DependencyFacet.class).getDirectDependency(apiDependency)
               .getScopeType());

      Assert.assertTrue(implProject.getFacet(DependencyFacet.class).hasDirectDependency(spiDependency));
      Assert.assertFalse(implProject.getFacet(DependencyFacet.class).hasDirectManagedDependency(spiDependency));
      Assert.assertTrue(implProject.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(spiDependency));
      Assert.assertEquals("provided", implProject.getFacet(DependencyFacet.class).getDirectDependency(spiDependency)
               .getScopeType());

      Assert.assertTrue(implProject.getFacet(DependencyFacet.class).hasDirectDependency(
               ForgeContainerAPIFacet.FORGE_API_DEPENDENCY));
      Assert.assertFalse(implProject.getFacet(DependencyFacet.class).hasDirectManagedDependency(
               ForgeContainerAPIFacet.FORGE_API_DEPENDENCY));
      Assert.assertTrue(implProject.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(
               ForgeContainerAPIFacet.FORGE_API_DEPENDENCY));

      /*
       * Verify api/ sub-module
       */
      Assert.assertEquals("../pom.xml", apiProject.getFacet(MavenFacet.class).getPOM().getParent().getRelativePath());

      Assert.assertTrue(apiProject.getFacet(DependencyFacet.class).hasDirectDependency(spiDependency));
      Assert.assertFalse(apiProject.getFacet(DependencyFacet.class).hasDirectManagedDependency(spiDependency));
      Assert.assertTrue(apiProject.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(spiDependency));
      Assert.assertEquals("provided", apiProject.getFacet(DependencyFacet.class).getDirectDependency(spiDependency)
               .getScopeType());

      Assert.assertTrue(apiProject.getFacet(DependencyFacet.class).hasDirectDependency(
               ForgeContainerAPIFacet.FORGE_API_DEPENDENCY));
      Assert.assertFalse(apiProject.getFacet(DependencyFacet.class).hasDirectManagedDependency(
               ForgeContainerAPIFacet.FORGE_API_DEPENDENCY));
      Assert.assertTrue(apiProject.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(
               ForgeContainerAPIFacet.FORGE_API_DEPENDENCY));

      /*
       * Verify spi/ sub-module
       */
      Assert.assertEquals("../pom.xml", spiProject.getFacet(MavenFacet.class).getPOM().getParent().getRelativePath());

      Assert.assertTrue(spiProject.getFacet(DependencyFacet.class).hasDirectDependency(
               ForgeContainerAPIFacet.FORGE_API_DEPENDENCY));
      Assert.assertFalse(spiProject.getFacet(DependencyFacet.class).hasDirectManagedDependency(
               ForgeContainerAPIFacet.FORGE_API_DEPENDENCY));
      Assert.assertTrue(spiProject.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(
               ForgeContainerAPIFacet.FORGE_API_DEPENDENCY));

      /*
       * Verify addon/ sub-module
       */
      Assert.assertEquals("../pom.xml", addonProject.getFacet(MavenFacet.class).getPOM().getParent().getRelativePath());

      Assert.assertTrue(addonProject.getFacet(DependencyFacet.class).hasDirectDependency(apiDependency));
      Assert.assertFalse(addonProject.getFacet(DependencyFacet.class).hasDirectManagedDependency(apiDependency));
      Assert.assertTrue(addonProject.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(apiDependency));
      Assert.assertNull(addonProject.getFacet(DependencyFacet.class).getDirectDependency(apiDependency)
               .getScopeType());

      Assert.assertTrue(addonProject.getFacet(DependencyFacet.class).hasDirectDependency(implDependency));
      Assert.assertFalse(addonProject.getFacet(DependencyFacet.class).hasDirectManagedDependency(implDependency));
      Assert.assertTrue(addonProject.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(implDependency));
      Assert.assertEquals("runtime", addonProject.getFacet(DependencyFacet.class).getDirectDependency(implDependency)
               .getScopeType());

      Assert.assertTrue(addonProject.getFacet(DependencyFacet.class).hasDirectDependency(spiDependency));
      Assert.assertFalse(addonProject.getFacet(DependencyFacet.class).hasDirectManagedDependency(spiDependency));
      Assert.assertTrue(addonProject.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(spiDependency));
      Assert.assertNull(addonProject.getFacet(DependencyFacet.class).getDirectDependency(spiDependency)
               .getScopeType());
      Assert.assertEquals("compile",
               addonProject.getFacet(DependencyFacet.class).getEffectiveDependency(spiDependency)
               .getScopeType());

      Assert.assertTrue(addonProject.getFacet(DependencyFacet.class).hasDirectDependency(
               ForgeContainerAPIFacet.FORGE_API_DEPENDENCY));
      Assert.assertFalse(addonProject.getFacet(DependencyFacet.class).hasDirectManagedDependency(
               ForgeContainerAPIFacet.FORGE_API_DEPENDENCY));
      Assert.assertTrue(addonProject.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(
               ForgeContainerAPIFacet.FORGE_API_DEPENDENCY));

      /*
       * Verify tests/ sub-module
       */

      Assert.assertEquals("../pom.xml", testsProject.getFacet(MavenFacet.class).getPOM().getParent().getRelativePath());

      Assert.assertTrue(testsProject.getFacet(DependencyFacet.class).hasDirectDependency(addonDependency));
      Assert.assertFalse(testsProject.getFacet(DependencyFacet.class).hasDirectManagedDependency(addonDependency));
      Assert.assertTrue(testsProject.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(addonDependency));
      Assert.assertNull(testsProject.getFacet(DependencyFacet.class).getDirectDependency(addonDependency)
               .getScopeType());
      Assert.assertEquals("compile",
               testsProject.getFacet(DependencyFacet.class).getEffectiveDependency(addonDependency)
                        .getScopeType());

      Assert.assertFalse(testsProject.getFacet(DependencyFacet.class).hasDirectDependency(spiDependency));
      Assert.assertFalse(testsProject.getFacet(DependencyFacet.class).hasDirectManagedDependency(spiDependency));
      Assert.assertTrue(testsProject.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(spiDependency));
      Assert.assertEquals("compile", testsProject.getFacet(DependencyFacet.class).getEffectiveDependency(spiDependency)
               .getScopeType());

      Assert.assertTrue(testsProject.getFacet(DependencyFacet.class).hasDirectDependency(
               ForgeContainerAPIFacet.FORGE_API_DEPENDENCY));
      Assert.assertFalse(testsProject.getFacet(DependencyFacet.class).hasDirectManagedDependency(
               ForgeContainerAPIFacet.FORGE_API_DEPENDENCY));
      Assert.assertTrue(testsProject.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(
               ForgeContainerAPIFacet.FORGE_API_DEPENDENCY));
   }
}
