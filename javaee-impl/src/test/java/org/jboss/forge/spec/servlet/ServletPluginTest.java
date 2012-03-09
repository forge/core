package org.jboss.forge.spec.servlet;

/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

import junit.framework.Assert;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.WebResourceFacet;
import org.jboss.forge.spec.javaee.ServletFacet;
import org.jboss.forge.test.AbstractShellTest;
import org.jboss.shrinkwrap.descriptor.api.spec.servlet.web.WebAppDescriptor;
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
      queueInputLines("y");
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
      queueInputLines("y");
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
