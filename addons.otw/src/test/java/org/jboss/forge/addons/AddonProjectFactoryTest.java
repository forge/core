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
import org.jboss.forge.arquillian.Addon;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.container.Forge;
import org.jboss.forge.container.addons.AddonId;
import org.jboss.forge.container.repositories.AddonDependencyEntry;
import org.jboss.forge.container.versions.SingleVersion;
import org.jboss.forge.projects.Project;
import org.jboss.forge.projects.ProjectFactory;
import org.jboss.forge.resource.DirectoryResource;
import org.jboss.forge.resource.ResourceFactory;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class AddonProjectFactoryTest
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
               addPackages(true, AddonProjectFactory.class.getPackage()).
               addAsAddonDependencies(
                        AddonDependencyEntry.create(AddonId.from("org.jboss.forge:maven", "2.0.0-SNAPSHOT")),
                        AddonDependencyEntry.create(AddonId.from("org.jboss.forge:projects", "2.0.0-SNAPSHOT"))
               );
   }

   @Inject
   private AddonProjectFactory addonProjectFactory;

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
      Project addonProject = addonProjectFactory.createAddonProject(project, forgeVersion,
               Collections.<AddonId> emptyList());
      Assert.assertNotNull(addonProject);

      DirectoryResource projectRoot = addonProject.getProjectRoot();
      Assert.assertTrue("ADDON module is missing", projectRoot.getChild("addon").exists());
      Assert.assertTrue("API module is missing", projectRoot.getChild("api").exists());
      Assert.assertTrue("IMPL module is missing", projectRoot.getChild("impl").exists());
      Assert.assertTrue("SPI module is missing", projectRoot.getChild("spi").exists());
      Assert.assertTrue("TESTS module is missing", projectRoot.getChild("tests").exists());
   }
}
