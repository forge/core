/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven;

import java.io.File;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.maven.resources.MavenModelResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.facets.WebResourcesFacet;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.container.simple.Service;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class MavenWebResourceFacetTest
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
               .addAsServiceProvider(Service.class, MavenWebResourceFacetTest.class);

      return archive;
   }

   private ProjectFactory projectFactory;
   private FacetFactory facetFactory;

   private Project project;

   @Before
   public void setUp()
   {
      projectFactory = SimpleContainer.getServices(getClass().getClassLoader(), ProjectFactory.class).get();
      facetFactory = SimpleContainer.getServices(getClass().getClassLoader(), FacetFactory.class).get();
      project = projectFactory.createTempProject();
   }

   @After
   public void tearDown()
   {
      if (project != null)
         project.getRoot().reify(DirectoryResource.class).delete(true);
   }

   @Test
   public void testWebResourceFacet()
   {
      WebResourcesFacet facet = facetFactory.install(project, WebResourcesFacet.class);
      Assert.assertTrue(project.hasFacet(WebResourcesFacet.class));
      Assert.assertTrue(facet.getWebRootDirectory().exists());
   }

   @Test
   public void testDefaultWebappFolder() throws Exception
   {
      WebResourcesFacet facet = facetFactory.install(project, WebResourcesFacet.class);
      DirectoryResource expected = project.getRoot().reify(DirectoryResource.class).getChildDirectory(
               "src" + File.separator + "main" + File.separator + "webapp");
      Assert.assertEquals(expected.getFullyQualifiedName(), facet.getWebRootDirectory().getFullyQualifiedName());
   }

   @Test
   public void testCustomWebappFolder() throws Exception
   {
      WebResourcesFacet facet = facetFactory.install(project, WebResourcesFacet.class);
      MavenModelResource pom = project.getRoot().reify(DirectoryResource.class).getChild("pom.xml")
               .reify(MavenModelResource.class);
      pom.setContents(
               "<project><modelVersion>4.0.0.</modelVersion><groupId>com.test</groupId><artifactId>testme</artifactId><version>1.0</version><build><plugins><plugin><artifactId>maven-war-plugin</artifactId><version>2.6</version><configuration><warSourceDirectory>WebContent</warSourceDirectory><failOnMissingWebXml>false</failOnMissingWebXml></configuration></plugin></plugins></build></project>");
      DirectoryResource expected = project.getRoot().reify(DirectoryResource.class).getChildDirectory(
               "WebContent");
      Assert.assertEquals(expected.getFullyQualifiedName(), facet.getWebRootDirectory().getFullyQualifiedName());
   }

}
