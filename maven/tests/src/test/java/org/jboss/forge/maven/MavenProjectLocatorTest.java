package org.jboss.forge.maven;

/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.Addon;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.container.Forge;
import org.jboss.forge.container.addons.AddonDependency;
import org.jboss.forge.container.addons.AddonId;
import org.jboss.forge.maven.projects.MavenProjectLocator;
import org.jboss.forge.resource.DirectoryResource;
import org.jboss.forge.resource.FileResource;
import org.jboss.forge.resource.ResourceFactory;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class MavenProjectLocatorTest
{
   @Deployment
   @Dependencies({
            @Addon(name = "org.jboss.forge:resources", version = "2.0.0-SNAPSHOT"),
            @Addon(name = "org.jboss.forge:projects", version = "2.0.0-SNAPSHOT"),
            @Addon(name = "org.jboss.forge:maven", version = "2.0.0-SNAPSHOT")
   })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependency.create(AddonId.from("org.jboss.forge:maven", "2.0.0-SNAPSHOT")),
                        AddonDependency.create(AddonId.from("org.jboss.forge:projects", "2.0.0-SNAPSHOT"))
               );

      return archive;
   }

   @Inject
   private ResourceFactory factory;

   @Inject
   private Forge forge;

   @Inject
   private MavenProjectLocator locator;

   @Test
   public void testInjectionNotNull()
   {
      Assert.assertNotNull(locator);
   }

   @Test
   public void testFindProject() throws Exception
   {
      DirectoryResource addonDir = factory.create(forge.getAddonDir()).reify(DirectoryResource.class);
      DirectoryResource projectDir = addonDir.createTempResource();
      FileResource<?> pomFile = projectDir.getChild("pom.xml").reify(FileResource.class);
      Assert.assertFalse(locator.containsProject(projectDir));
      pomFile.createNewFile();
      pomFile.setContents(getClass().getClassLoader().getResourceAsStream("/pom-template.xml"));

      Assert.assertTrue(locator.containsProject(projectDir));

      projectDir.delete(true);
   }
}
