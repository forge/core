/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.maven;

import java.io.File;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.maven.resources.MavenPomResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.facets.WebResourcesFacet;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class MavenWebResourceFacetTest
{

   @Deployment
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.addon:projects", version = "2.0.0-SNAPSHOT"),
            @AddonDependency(name = "org.jboss.forge.addon:maven", version = "2.0.0-SNAPSHOT")
   })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi", "2.0.0-SNAPSHOT"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:maven", "2.0.0-SNAPSHOT"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:projects", "2.0.0-SNAPSHOT")
               );

      return archive;
   }

   @Inject
   private ProjectFactory projectFactory;

   @Inject
   private ResourceFactory resourceFactory;

   @Inject
   private FacetFactory facetFactory;

   @Test
   public void testWebResourceFacet()
   {
      DirectoryResource target = resourceFactory.create(DirectoryResource.class, new File("target"));
      Project project = projectFactory.createProject(target.getOrCreateChildDirectory("project"));
      WebResourcesFacet facet = facetFactory.install(project, WebResourcesFacet.class);
      Assert.assertTrue(project.hasFacet(WebResourcesFacet.class));
      Assert.assertTrue(facet.getWebRootDirectory().exists());
   }

   @Test
   public void testDefaultWebappFolder() throws Exception
   {
      DirectoryResource target = resourceFactory.create(DirectoryResource.class, new File("target"));
      Project project = projectFactory.createProject(target.getOrCreateChildDirectory("project"));
      WebResourcesFacet facet = facetFactory.install(project, WebResourcesFacet.class);
      DirectoryResource expected = project.getProjectRoot().getChildDirectory(
               "src" + File.separator + "main" + File.separator + "webapp");
      Assert.assertEquals(expected.getFullyQualifiedName(), facet.getWebRootDirectory().getFullyQualifiedName());
   }

   @Test
   public void testCustomWebappFolder() throws Exception
   {
      DirectoryResource target = resourceFactory.create(DirectoryResource.class, new File("target"));
      Project project = projectFactory.createProject(target.getOrCreateChildDirectory("project"));
      WebResourcesFacet facet = facetFactory.install(project, WebResourcesFacet.class);
      MavenPomResource pom = project.getProjectRoot().getChild("pom.xml").reify(MavenPomResource.class);
      pom.setContents("<project><modelVersion>4.0.0.</modelVersion><groupId>com.test</groupId><artifactId>testme</artifactId><version>1.0</version><build><plugins><plugin><artifactId>maven-war-plugin</artifactId><version>2.4</version><configuration><warSourceDirectory>WebContent</warSourceDirectory><failOnMissingWebXml>false</failOnMissingWebXml></configuration></plugin></plugins></build></project>");
      DirectoryResource expected = project.getProjectRoot().getChildDirectory(
               "WebContent");
      Assert.assertEquals(expected.getFullyQualifiedName(), facet.getWebRootDirectory().getFullyQualifiedName());
   }

}
