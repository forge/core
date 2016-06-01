/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven;

import java.io.File;
import java.util.List;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.dependencies.Coordinate;
import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.dependencies.DependencyInstaller;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.container.simple.Service;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.FileAsset;
import org.junit.Assert;
import org.junit.Before;
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
               .addAsServiceProvider(Service.class, MavenDependencyInstallerTest.class);

      return archive;
   }

   private ProjectFactory projectFactory;
   private DependencyInstaller installer;

   @Before
   public void setUp()
   {
      projectFactory = SimpleContainer.getServices(getClass().getClassLoader(), ProjectFactory.class).get();
      installer = SimpleContainer.getServices(getClass().getClassLoader(), DependencyInstaller.class).get();
   }

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
