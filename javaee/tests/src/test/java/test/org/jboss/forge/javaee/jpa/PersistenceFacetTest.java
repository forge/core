/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.org.jboss.forge.javaee.jpa;

import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.Addon;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.facets.FacetFactory;
import org.jboss.forge.javaee.spec.PersistenceFacet;
import org.jboss.forge.projects.Project;
import org.jboss.forge.projects.ProjectFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import test.org.jboss.forge.javaee.JavaEETestHelper;

@RunWith(Arquillian.class)
public class PersistenceFacetTest
{
   @Deployment
   @Dependencies({
            @Addon(name = "org.jboss.forge:javaee", version = "2.0.0-SNAPSHOT"),
            @Addon(name = "org.jboss.forge:maven", version = "2.0.0-SNAPSHOT")
   })
   public static ForgeArchive getDeployment()
   {
      return JavaEETestHelper.getDeployment();
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

   @Test
   public void testInstall()
   {
      facetFactory.install(PersistenceFacet.class, project);
      Assert.assertTrue(project.hasFacet(PersistenceFacet.class));
   }

   @Test
   @Ignore("Conflicts with Arquillian + JBoss Modules")
   public void testCanWritePersistenceConfigFile() throws Exception
   {
      facetFactory.install(PersistenceFacet.class, project);
      PersistenceFacet persistence = project.getFacet(PersistenceFacet.class);
      assertNotNull(persistence);

      Assert.assertEquals("2.0", persistence.getConfig().getVersion());
   }

}
