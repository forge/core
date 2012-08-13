/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.cdi;

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
