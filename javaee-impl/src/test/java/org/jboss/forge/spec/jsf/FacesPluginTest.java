/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.jsf;

import java.util.List;

import org.jboss.forge.project.Project;
import org.jboss.forge.project.packaging.PackagingType;
import org.jboss.forge.spec.javaee.FacesFacet;
import org.jboss.forge.spec.javaee.ServletFacet;
import org.jboss.forge.test.AbstractShellTest;
import org.jboss.shrinkwrap.descriptor.api.spec.servlet.web.WebAppDescriptor;
import org.junit.Assert;
import org.junit.Test;

public class FacesPluginTest extends AbstractShellTest
{

   @Test
   public void testFacesConfig() throws Exception
   {
      Project project = initializeJavaProject();
      queueInputLines("Y", "", "Y", "Y");
      getShell().execute("faces setup");
      Assert.assertTrue(project.getProjectRoot().getChild("src/main/webapp/WEB-INF/web.xml").exists());
   }

   @Test
   public void testFacesServlet2_3() throws Exception
   {
      Project project = initializeProject(PackagingType.WAR);
      ServletFacet servletFacet = project.getFacet(ServletFacet.class);
      WebAppDescriptor config = servletFacet.getConfig();
      config.version("2.3");
      servletFacet.saveConfig(config);
      queueInputLines("Y", "", "Y", "Y");
      getShell().execute("faces setup");
      Assert.assertTrue(project.hasFacet(FacesFacet.class));

      FacesFacet facet = project.getFacet(FacesFacet.class);
      List<String> facesServletMappings = facet.getFacesServletMappings();
      Assert.assertNotNull(facesServletMappings);
      Assert.assertFalse(facesServletMappings.isEmpty());
   }

   @Test
   public void testFacesServlet3() throws Exception
   {
      Project project = initializeProject(PackagingType.WAR);
      ServletFacet servletFacet = project.getFacet(ServletFacet.class);
      WebAppDescriptor config = servletFacet.getConfig();
      config.version("3.0");
      servletFacet.saveConfig(config);
      queueInputLines("Y", "", "Y", "N");
      getShell().execute("faces setup");
      Assert.assertTrue(project.hasFacet(FacesFacet.class));

      FacesFacet facet = project.getFacet(FacesFacet.class);
      List<String> facesServletMappings = facet.getFacesServletMappings();
      Assert.assertNotNull(facesServletMappings);
      Assert.assertTrue(facesServletMappings.isEmpty());
   }

}