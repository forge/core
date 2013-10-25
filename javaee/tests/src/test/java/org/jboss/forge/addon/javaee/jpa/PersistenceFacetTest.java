/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.jpa;

import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.facets.FacetIsAmbiguousException;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.descriptor.api.persistence.PersistenceCommonDescriptor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class PersistenceFacetTest
{
   @Deployment
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.addon:javaee"),
            @AddonDependency(name = "org.jboss.forge.addon:maven")
   })
   public static ForgeArchive getDeployment()
   {
      return ShrinkWrap.create(ForgeArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:projects"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:javaee"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:maven")
               );
   }

   private Project project;

   @Inject
   ProjectFactory projectFactory;

   @Inject
   FacetFactory facetFactory;

   @Before
   public void setUp()
   {
      project = projectFactory.createTempProject();
   }

   @Test(expected = FacetIsAmbiguousException.class)
   public void testInstall()
   {
      facetFactory.install(project, JPAFacet.class);
   }

   @Test
   @SuppressWarnings({ "rawtypes", "unchecked" })
   public void testCanWritePersistenceConfigFile() throws Exception
   {
      facetFactory.install(project, JPAFacet_2_0.class);
      JPAFacet<PersistenceCommonDescriptor> persistence = project.getFacet(JPAFacet.class);
      assertNotNull(persistence);

      Assert.assertEquals("2.0", persistence.getConfig().getVersion());
   }

}
