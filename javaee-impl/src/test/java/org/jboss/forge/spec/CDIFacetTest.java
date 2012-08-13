/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.ScopeType;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.spec.javaee.CDIFacet;
import org.jboss.forge.test.SingletonAbstractShellTest;
import org.jboss.shrinkwrap.descriptor.api.spec.cdi.beans.BeansDescriptor;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class CDIFacetTest extends SingletonAbstractShellTest
{
   private static final String JAVAEE6_SPECS_DEPENDENCY = "org.jboss.spec:jboss-javaee-6.0";

   @Test
   public void testBeansXMLCreatedWhenInstalled() throws Exception
   {
      Project project = initializeJavaProject();
      queueInputLines("");
      getShell().execute("beans setup");
      assertTrue(project.hasFacet(CDIFacet.class));
      BeansDescriptor config = project.getFacet(CDIFacet.class).getConfig();

      assertNotNull(config);
   }

   @Test
   public void testBeansXMLMovedWhenPackagingTypeChanged() throws Exception
   {
      Project project = initializeJavaProject();
      queueInputLines("");
      getShell().execute("beans setup");
      FileResource<?> config = project.getFacet(CDIFacet.class).getConfigFile();

      queueInputLines("");
      // FIXME replace with ServletPlugin "servlet setup"
      getShell().execute("project install-facet forge.spec.servlet");
      FileResource<?> newConfig = project.getFacet(CDIFacet.class).getConfigFile();

      assertNotNull(config);
      assertNotNull(newConfig);
      assertTrue(config.getFullyQualifiedName().contains("META-INF"));
      assertTrue(newConfig.getFullyQualifiedName().contains("WEB-INF"));
      assertFalse(config.getFullyQualifiedName().equals(newConfig.getFullyQualifiedName()));
   }

   @Test
   public void testCdiDependencyScopeProvidedForWebProject() throws Exception
   {
      DependencyBuilder dependency = DependencyBuilder.create(JAVAEE6_SPECS_DEPENDENCY)
               .setScopeType(ScopeType.PROVIDED);
      Project project = initializeJavaProject();

      queueInputLines("");
      getShell().execute("project install-facet forge.maven.WebResourceFacet");

      queueInputLines("");
      getShell().execute("beans setup");

      DependencyFacet dependencyFacet = project.getFacet(DependencyFacet.class);

      assertTrue(dependencyFacet.hasDirectManagedDependency(dependency));
      assertTrue(dependencyFacet.hasEffectiveDependency(DependencyBuilder.create("javax.enterprise:cdi-api")));
      assertTrue(dependencyFacet.hasEffectiveDependency(DependencyBuilder.create("javax.inject:javax.inject")));
   }

   @Test
   public void testDependencyAlreadyInstalled() throws Exception
   {
      Project project = initializeJavaProject();
      DependencyFacet dependencyFacet = project.getFacet(DependencyFacet.class);
      dependencyFacet.addDirectDependency(DependencyBuilder.create("javax.enterprise:cdi-api").setVersion("1.0-SP4"));

      queueInputLines("");
      getShell().execute("beans setup");
      assertTrue(dependencyFacet.hasEffectiveDependency(DependencyBuilder.create("javax.enterprise:cdi-api")));
      assertTrue(dependencyFacet.hasDirectManagedDependency(DependencyBuilder.create(JAVAEE6_SPECS_DEPENDENCY)));
   }

}
