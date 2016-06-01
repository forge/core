/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.security.ui;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.forge.addon.javaee.ProjectHelper;
import org.jboss.forge.addon.javaee.servlet.ServletFacet;
import org.jboss.forge.addon.javaee.servlet.ServletFacet_2_5;
import org.jboss.forge.addon.javaee.servlet.ServletFacet_3_0;
import org.jboss.forge.addon.javaee.servlet.ServletFacet_3_1;
import org.jboss.forge.addon.parser.java.projects.JavaWebProjectType;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.ui.test.UITestHarness;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Before;

public abstract class AbstractSecurityCommandTest
{
   @Inject
   protected ProjectHelper projectHelper;

   @Inject
   protected JavaWebProjectType javaWebProjectType;

   @Inject
   protected UITestHarness testHarness;

   protected Project project;

   @Deployment
   @AddonDependencies
   public static AddonArchive getDeployment()
   {
      return ShrinkWrap
               .create(AddonArchive.class)
               .addBeansXML()
               .addClass(ProjectHelper.class)
               .addClass(AbstractSecurityCommandTest.class);
   }

   @Before
   public void setup()
   {
      project = projectHelper.createWebProject();
   }

   @SuppressWarnings("unchecked")
   protected <T extends ServletFacet<?>> T installServlet(Class<T> servletFacetClass)
   {
      if (servletFacetClass.equals(ServletFacet_2_5.class))
      {
         return (T) projectHelper.installServlet_2_5(project);
      }
      else if (servletFacetClass.equals(ServletFacet_3_0.class))
      {
         return (T) projectHelper.installServlet_3_0(project);
      }
      else if (servletFacetClass.equals(ServletFacet_3_1.class))
      {
         return (T) projectHelper.installServlet_3_1(project);
      }
      throw new IllegalArgumentException("Only supported Servlet implementations are 2.5, 3.0, 3.1");
   }

}
