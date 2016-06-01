/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.projects.facets;

import static org.hamcrest.CoreMatchers.equalTo;

import java.util.Properties;

import org.apache.maven.model.Model;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.maven.projects.MavenFacet;
import org.jboss.forge.addon.parser.java.facets.JavaCompilerFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.container.simple.Service;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
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
public class MavenJavaCompilerFacetTest
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
               .addAsServiceProvider(Service.class, MavenJavaCompilerFacetTest.class);

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
   public void testCompilerPropertiesSet() throws Exception
   {
      Project project = projectFactory.createTempProject();
      facetFactory.install(project, JavaCompilerFacet.class);

      MavenFacet facet = project.getFacet(MavenFacet.class);
      Model model = facet.getModel();
      Properties properties = model.getProperties();
      Assert.assertThat(properties.getProperty("maven.compiler.source"),
               equalTo(JavaCompilerFacet.DEFAULT_COMPILER_VERSION.toString()));
      Assert.assertThat(properties.getProperty("maven.compiler.target"),
               equalTo(JavaCompilerFacet.DEFAULT_COMPILER_VERSION.toString()));
      Assert.assertThat(properties.getProperty("project.build.sourceEncoding"), equalTo("UTF-8"));
   }
}
