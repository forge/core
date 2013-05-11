/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.maven;

import java.io.File;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.Addon;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.container.addons.AddonId;
import org.jboss.forge.container.repositories.AddonDependencyEntry;
import org.jboss.forge.facets.FacetFactory;
import org.jboss.forge.projects.Project;
import org.jboss.forge.projects.ProjectFactory;
import org.jboss.forge.projects.facets.WebResourceFacet;
import org.jboss.forge.resource.DirectoryResource;
import org.jboss.forge.resource.ResourceFactory;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class MavenWebResourceFacetTest
{

   @Deployment
   @Dependencies({
            @Addon(name = "org.jboss.forge:projects", version = "2.0.0-SNAPSHOT"),
            @Addon(name = "org.jboss.forge:maven", version = "2.0.0-SNAPSHOT")
   })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create(AddonId.from("org.jboss.forge:maven", "2.0.0-SNAPSHOT")),
                        AddonDependencyEntry.create(AddonId.from("org.jboss.forge:projects", "2.0.0-SNAPSHOT"))
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
      WebResourceFacet facet = facetFactory.install(WebResourceFacet.class, project);
      Assert.assertTrue(project.hasFacet(WebResourceFacet.class));
      Assert.assertTrue(facet.getWebRootDirectory().exists());
   }

}
