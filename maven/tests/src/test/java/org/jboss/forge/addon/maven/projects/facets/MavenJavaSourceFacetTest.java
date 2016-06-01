/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.projects.facets;

import static org.hamcrest.CoreMatchers.is;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.ResourceException;
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
public class MavenJavaSourceFacetTest
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
               .addAsServiceProvider(Service.class, MavenJavaSourceFacetTest.class);

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

   @Test(expected = ResourceException.class)
   public void testNullRelativePath() throws Exception
   {
      Project project = projectFactory.createTempProject();
      JavaSourceFacet facet = facetFactory.install(project, JavaSourceFacet.class);
      facet.getJavaResource((String) null);
   }

   @Test(expected = ResourceException.class)
   public void testEmptyRelativePath() throws Exception
   {
      Project project = projectFactory.createTempProject();
      JavaSourceFacet facet = facetFactory.install(project, JavaSourceFacet.class);
      facet.getJavaResource("");
   }

   @Test
   public void testBasePackageCreatedOnFacetInstall() throws Exception
   {
      Project project = projectFactory.createTempProject();
      JavaSourceFacet facet = facetFactory.install(project, JavaSourceFacet.class);
      String projectGroupName = project.getFacet(MetadataFacet.class).getProjectGroupName();
      DirectoryResource pkg = facet.getPackage(projectGroupName);
      Assert.assertThat(pkg.exists(), is(true));
   }

   @Test
   public void testSaveJavaSourceUnformatted() throws Exception
   {
      Project project = projectFactory.createTempProject();
      JavaSourceFacet facet = facetFactory.install(project, JavaSourceFacet.class);
      String data = "public class Foo{String name;\n\n\n\n\n\tint bar}";
      JavaClassSource clazz = Roaster.parse(JavaClassSource.class, data);
      JavaResource resource = facet.saveJavaSourceUnformatted(clazz);
      Assert.assertEquals(data, resource.getContents());
   }

   @Test
   public void testSaveTestJavaSourceUnformatted() throws Exception
   {
      Project project = projectFactory.createTempProject();
      JavaSourceFacet facet = facetFactory.install(project, JavaSourceFacet.class);
      String data = "public class Foo{String name;int bar}";
      JavaClassSource clazz = Roaster.parse(JavaClassSource.class, data);
      JavaResource resource = facet.saveTestJavaSourceUnformatted(clazz);
      Assert.assertEquals(data, resource.getContents());
   }

}
