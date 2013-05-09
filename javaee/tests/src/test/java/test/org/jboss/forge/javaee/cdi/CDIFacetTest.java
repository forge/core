/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.org.jboss.forge.javaee.cdi;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.Addon;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.container.Forge;
import org.jboss.forge.container.addons.AddonId;
import org.jboss.forge.container.repositories.AddonDependencyEntry;
import org.jboss.forge.facets.FacetFactory;
import org.jboss.forge.javaee.spec.CDIFacet;
import org.jboss.forge.projects.Project;
import org.jboss.forge.projects.ProjectFactory;
import org.jboss.forge.resource.DirectoryResource;
import org.jboss.forge.resource.ResourceFactory;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.descriptor.api.beans10.BeansDescriptor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class CDIFacetTest
{
   @Deployment
   @Dependencies({
            @Addon(name = "org.jboss.forge:javaee", version = "2.0.0-SNAPSHOT"),
            @Addon(name = "org.jboss.forge:maven", version = "2.0.0-SNAPSHOT")
   })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create(AddonId.from("org.jboss.forge:javaee", "2.0.0-SNAPSHOT")),
                        AddonDependencyEntry.create(AddonId.from("org.jboss.forge:maven", "2.0.0-SNAPSHOT"))
               );
      return archive;
   }

   @Inject
   private ProjectFactory projectFactory;

   @Inject
   private ResourceFactory resourceFactory;

   @Inject
   private Forge forge;

   @Inject
   private FacetFactory facetFactory;

   private Project project;

   @Before
   public void beforeTest()
   {
      DirectoryResource addonDir = resourceFactory.create(forge.getRepositories().get(0).getRootDirectory()).reify(
               DirectoryResource.class);
      DirectoryResource projectDir = addonDir.createTempResource();
      this.project = projectFactory.createProject(projectDir);
      projectDir.deleteOnExit();
   }

   @After
   public void afterTest()
   {
      project.getProjectRoot().delete(true);
   }

   @Test
   public void testBeansXMLCreatedWhenInstalled() throws Exception
   {
      CDIFacet cdiFacet = facetFactory.create(CDIFacet.class, project);
      assertNotNull(cdiFacet);
      project.install(cdiFacet);
      assertTrue(project.hasFacet(CDIFacet.class));
      BeansDescriptor config = project.getFacet(CDIFacet.class).getConfig();
      assertNotNull(config);
   }

}