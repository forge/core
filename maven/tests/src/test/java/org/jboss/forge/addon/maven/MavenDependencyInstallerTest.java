/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.maven;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.dependencies.Coordinate;
import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.dependencies.DependencyInstaller;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.FileAsset;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class MavenDependencyInstallerTest
{
   @Deployment
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.addon:resources"),
            @AddonDependency(name = "org.jboss.forge.addon:projects"),
            @AddonDependency(name = "org.jboss.forge.addon:maven")
   })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .add(new FileAsset(new File("src/test/resources/pom-template.xml")),
                        "org/jboss/forge/addon/maven/pom-template.xml")
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:maven"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:projects")
               );

      return archive;
   }

   @Inject
   private ProjectFactory projectFactory;

   @Inject
   private DependencyInstaller installer;

   @Test
   public void testDependencyManagementWithProjectProperties()
   {
      Project project = projectFactory.createTempProject();
      Dependency dep = DependencyBuilder
               .create("org.apache.deltaspike.core:deltaspike-core-api:${deltaspike.version}:compile");
      installer.install(project, dep);
      DependencyFacet facet = project.getFacet(DependencyFacet.class);
      List<Dependency> managedDependencies = facet.getManagedDependencies();
      Assert.assertEquals(1, managedDependencies.size());
      Dependency dependency = managedDependencies.get(0);
      Coordinate coordinate = dependency.getCoordinate();
      Assert.assertEquals("org.apache.deltaspike.core", coordinate.getGroupId());
      Assert.assertEquals("deltaspike-core-api", coordinate.getArtifactId());
      Assert.assertEquals("${deltaspike.version}", coordinate.getVersion());
   }
}
