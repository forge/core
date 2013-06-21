/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.servlet;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.javaee.JavaEETestHelper;
import org.jboss.forge.addon.javaee.facets.ServletFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
import org.jboss.forge.arquillian.Addon;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.shrinkwrap.descriptor.api.webapp30.WebAppDescriptor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
public class ServletFacetTest
{
   @Deployment
   @Dependencies({
            @Addon(name = "org.jboss.forge.addon:javaee", version = "2.0.0-SNAPSHOT"),
            @Addon(name = "org.jboss.forge.addon:maven", version = "2.0.0-SNAPSHOT"),
            @Addon(name = "org.jboss.forge.addon:projects", version = "2.0.0-SNAPSHOT")
   })
   public static ForgeArchive getDeployment()
   {
      return JavaEETestHelper.getDeployment();
   }

   @Inject
   private ProjectFactory projectFactory;

   @Inject
   private FacetFactory facetFactory;

   @Test
   public void testWebXMLCreatedWhenInstalled() throws Exception
   {
      Project project = projectFactory.createTempProject();
      ServletFacet facet = facetFactory.install(project, ServletFacet.class);
      assertNotNull(facet);
      assertTrue(project.hasFacet(ServletFacet.class));
      WebAppDescriptor config = facet.getConfig();
      assertNotNull(config);
      String projectName = project.getFacet(MetadataFacet.class).getProjectName();
      Assert.assertFalse(config.getAllDisplayName().isEmpty());
      Assert.assertEquals(projectName, config.getAllDisplayName().get(0));
   }
}