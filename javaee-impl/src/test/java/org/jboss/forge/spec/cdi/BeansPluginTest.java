package org.jboss.forge.spec.cdi;

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
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.ResourceFacet;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.spec.javaee.CDIFacet;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class BeansPluginTest extends AbstractShellTest
{
   @Test
   public void testBeansSetup() throws Exception
   {
      Project project = initializeJavaProject();
      queueInputLines("n", "");
      getShell().execute("beans setup");

      project.getFacet(CDIFacet.class).getConfig();
      Assert.assertTrue(project.getFacet(DependencyFacet.class).hasDirectManagedDependency(
               DependencyBuilder.create("org.jboss.spec:jboss-javaee-6.0")));
      Assert.assertTrue(project.getFacet(ResourceFacet.class).getResource("META-INF/beans.xml").exists());
   }

   @Test
   public void testBeansSetupProvidedScope() throws Exception
   {
      Project project = initializeJavaProject();
      queueInputLines("y", "");
      getShell().execute("beans setup");

      project.getFacet(CDIFacet.class).getConfig();
      Assert.assertTrue(project.getFacet(DependencyFacet.class).hasDirectManagedDependency(
               DependencyBuilder.create("org.jboss.spec:jboss-javaee-6.0")));
      Assert.assertTrue(project.getFacet(ResourceFacet.class).getResource("META-INF/beans.xml").exists());
   }

   @Test
   public void testNewBean() throws Exception
   {
      Project project = initializeJavaProject();
      queueInputLines("y", "");
      getShell().execute("beans setup");
      getShell().execute("beans new-bean --type foo.beans.ExampleBean --scoped REQUEST");

      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      JavaResource resource = java.getJavaResource("foo.beans.ExampleBean");

      Assert.assertTrue(resource.exists());
      JavaSource<?> source = resource.getJavaSource();

      Assert.assertNotNull(source);
      Assert.assertEquals("foo.beans", source.getPackage());
      project.getFacet(CDIFacet.class).getConfig();
   }
}
