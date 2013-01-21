/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.servlet;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.WebResourceFacet;
import org.jboss.forge.spec.javaee.ServletFacet;
import org.jboss.forge.test.AbstractShellTest;
import org.jboss.shrinkwrap.descriptor.api.spec.servlet.web.WebAppDescriptor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class ServletPluginTest extends AbstractShellTest
{
   @Test
   public void testServletSetupDoesNotInitiallyCreateWebXML() throws Exception
   {
      Project project = initializeJavaProject();
      queueInputLines("y", "");
      getShell().execute("servlet setup");

      Assert.assertTrue(project.hasFacet(ServletFacet.class));
      Assert.assertTrue(project.getFacet(DependencyFacet.class).hasEffectiveDependency(
               DependencyBuilder.create("org.jboss.spec.javax.servlet:jboss-servlet-api_3.0_spec")));
      Assert.assertFalse(project.getFacet(WebResourceFacet.class).getWebResource("WEB-INF/web.xml").exists());
   }

   @Test
   public void testServletLazilyCreatesWebXMLOnSave() throws Exception
   {
      Project project = initializeJavaProject();
      queueInputLines("y", "");
      getShell().execute("setup servlet");

      Assert.assertTrue(project.getFacet(DependencyFacet.class).hasEffectiveDependency(
               DependencyBuilder.create("org.jboss.spec.javax.servlet:jboss-servlet-api_3.0_spec")));
      Assert.assertTrue(project.hasFacet(ServletFacet.class));

      WebAppDescriptor config = project.getFacet(ServletFacet.class).getConfig();
      Assert.assertFalse(project.getFacet(WebResourceFacet.class).getWebResource("WEB-INF/web.xml").exists());
      project.getFacet(ServletFacet.class).saveConfig(config);
      Assert.assertTrue(project.getFacet(WebResourceFacet.class).getWebResource("WEB-INF/web.xml").exists());
   }
}
