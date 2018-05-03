/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.projects.facets;

import java.util.Arrays;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.facets.JavaTargetFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.container.simple.Service;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class MavenJavaTargetFacetTest
{

   @Deployment
   @AddonDependencies({
            @AddonDependency(name = "org.jboss.forge.addon:parser-java"),
            @AddonDependency(name = "org.jboss.forge.addon:projects"),
            @AddonDependency(name = "org.jboss.forge.addon:maven"),
            @AddonDependency(name = "org.jboss.forge.furnace.container:simple")
   })
   public static AddonArchive getDeployment()
   {
      AddonArchive archive = ShrinkWrap
               .create(AddonArchive.class)
               .addAsServiceProvider(Service.class, MavenJavaTargetFacetTest.class);

      return archive;
   }

   private ProjectFactory projectFactory;
   private FacetFactory facetFactory;

   @Before
   public void setUp()
   {
      projectFactory = SimpleContainer.getServices(getClass().getClassLoader(), ProjectFactory.class).get();
      facetFactory = SimpleContainer.getServices(getClass().getClassLoader(), FacetFactory.class).get();
   }

   @Test
   public void testFacetInstallationBuildsProject()
   {
      Project project = projectFactory.createTempProject(Arrays.asList(JavaSourceFacet.class));
      JavaSourceFacet sourceFacet = project.getFacet(JavaSourceFacet.class);
      JavaResource javaResource = sourceFacet.getJavaResource("com.example.App");
      javaResource.setContents(Roaster.create(JavaClassSource.class).setName("App").setPackage("com.example"));
      Assert.assertFalse(project.hasFacet(JavaTargetFacet.class));
      JavaTargetFacet facet = facetFactory.install(project, JavaTargetFacet.class);
      Assert.assertTrue(facet.getTargetDirectory().getChild("com/example/App.class").exists());
   }

}
