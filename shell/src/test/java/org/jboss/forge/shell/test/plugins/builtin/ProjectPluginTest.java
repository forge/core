/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.test.plugins.builtin;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.matchers.JUnitMatchers.hasItem;

import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.ScopeType;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.packaging.PackagingType;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:macdude357@gmail.com">Tim Pedone</a>
 */
@RunWith(Arquillian.class)
public class ProjectPluginTest extends AbstractShellTest
{
   @Test
   public void testAddProperty() throws Exception
   {
      initializeJavaProject();

      getShell().execute("project set-property --name forge.version --value 1.0.2.Final");
      final DependencyFacet dependencyFacet = getProject().getFacet(DependencyFacet.class);

      assertThat(dependencyFacet.getProperty("forge.version"), is("1.0.2.Final"));
   }

   @Test
   public void testAddDependencyUsingProperty() throws Exception
   {
      initializeJavaProject();

      getShell().execute("project set-property --name arquillian.bom.version --value 1.0.0.CR7");
      getShell().execute("project add-dependency \"org.jboss.arquillian:arquillian-bom:${arquillian.bom.version}\"");

      DependencyFacet deps = getProject().getFacet(DependencyFacet.class);
      Dependency arquillianBomDep = DependencyBuilder.create("org.jboss.arquillian:arquillian-bom:1.0.0.CR7")
               .setPackagingType(PackagingType.JAR).setScopeType(ScopeType.COMPILE);

      assertThat(deps.getEffectiveDependencies(), hasItem(arquillianBomDep));
   }

   @Test
   public void testAddDependencyFromManagedParent() throws Exception
   {
      initializeJavaProject();

      // commons-httpclient v 3.1 is in the depManagement section of the weld-api-bom
      MavenCoreFacet mvnFacet = getProject().getFacet(MavenCoreFacet.class);
      Parent parent = new Parent();
      parent.setArtifactId("weld-api-bom");
      parent.setGroupId("org.jboss.weld");
      parent.setVersion("1.1.Final");
      Model pom = mvnFacet.getPOM();
      pom.setParent(parent);
      mvnFacet.setPOM(pom);

      DependencyBuilder dep = DependencyBuilder.create();
      dep.setArtifactId("commons-httpclient");
      dep.setGroupId("commons-httpclient");
      dep.setVersion("3.1");
      dep.setScopeType(ScopeType.COMPILE);
      dep.setPackagingType("jar");

      DependencyFacet facet = getProject().getFacet(DependencyFacet.class);
      assertNull(facet.getEffectiveDependency(dep));

      queueInputLines("Y");
      getShell().execute("project add-dependency commons-httpclient:commons-httpclient");

      Dependency newDep = facet.getEffectiveDependency(dep);

      assertEquals(newDep.toCoordinates(), dep.toCoordinates());
   }

   /**
    * Tests that if a dependency is managed by a parent and a different version is managed in the pom itself, then the
    * local version will be used
    * 
    * @throws Exception
    */
   @Test
   public void testAddDependencyFromLocalAndManagedParent() throws Exception
   {
      initializeJavaProject();
      getShell().execute("project add-managed-dependency commons-httpclient:commons-httpclient:3.0");

      // commons-httpclient v 3.1 is in the depManagement section of the weld-api-bom
      MavenCoreFacet mvnFacet = getProject().getFacet(MavenCoreFacet.class);
      Parent parent = new Parent();
      parent.setArtifactId("weld-api-bom");
      parent.setGroupId("org.jboss.weld");
      parent.setVersion("1.1.Final");
      Model pom = mvnFacet.getPOM();
      pom.setParent(parent);
      mvnFacet.setPOM(pom);

      DependencyBuilder dep = DependencyBuilder.create();
      dep.setArtifactId("commons-httpclient");
      dep.setGroupId("commons-httpclient");
      dep.setVersion("3.0");
      dep.setScopeType(ScopeType.COMPILE);
      dep.setPackagingType("jar");

      DependencyFacet facet = getProject().getFacet(DependencyFacet.class);
      assertNull(facet.getEffectiveDependency(dep));

      queueInputLines("Y");
      getShell().execute("project add-dependency commons-httpclient:commons-httpclient");

      Dependency newDep = facet.getEffectiveDependency(dep);

      assertEquals(newDep.toCoordinates(), dep.toCoordinates());
   }

   /**
    * Tests that a dependency managed by an imported pom will be used.
    * 
    * @throws Exception
    */
   @Test
   public void testAddDependencyFromManagedImport() throws Exception
   {
      initializeJavaProject();
      // commons-httpclient v 3.1 is in the depManagement section of the weld-api-bom
      getShell().execute("project add-managed-dependency org.jboss.weld:weld-api-bom:1.1.Final:import:pom");

      MavenCoreFacet mvnFacet = getProject().getFacet(MavenCoreFacet.class);

      Model pom = mvnFacet.getPOM();
      mvnFacet.setPOM(pom);

      DependencyBuilder dep = DependencyBuilder.create();
      dep.setArtifactId("commons-httpclient");
      dep.setGroupId("commons-httpclient");
      dep.setVersion("3.1");
      dep.setScopeType(ScopeType.COMPILE);
      dep.setPackagingType("jar");

      DependencyFacet facet = getProject().getFacet(DependencyFacet.class);
      assertNull(facet.getEffectiveDependency(dep));

      queueInputLines("Y");
      getShell().execute("project add-dependency commons-httpclient:commons-httpclient");

      Dependency newDep = facet.getEffectiveDependency(dep);

      assertEquals(newDep.toCoordinates(), dep.toCoordinates());
   }

   /**
    * Tests that a dependency that is managed in the local pom will be used.
    * 
    * @throws Exception
    */
   @Test
   public void testAddDependencyFromManaged() throws Exception
   {
      Project project = initializeJavaProject();
      getShell().execute("project add-managed-dependency com.ocpsoft:prettyfaces-jsf2:3.2.0");
      DependencyFacet facet = project.getFacet(DependencyFacet.class);

      DependencyBuilder dep = DependencyBuilder.create();
      dep.setArtifactId("prettyfaces-jsf2");
      dep.setGroupId("com.ocpsoft");
      dep.setVersion("3.2.0");
      dep.setScopeType("compile");
      dep.setPackagingType("jar");

      assertNull(facet.getEffectiveDependency(dep));
      queueInputLines("Y");
      getShell().execute("project add-dependency com.ocpsoft:prettyfaces-jsf2");

      Dependency newDep = facet.getEffectiveDependency(dep);

      assertEquals(newDep.toCoordinates(), dep.toCoordinates());
   }

   /**
    * Tests overriding managed dependency with a different version.
    * 
    * @throws Exception
    */
   @Test
   public void testAddDependencyOverrideManaged() throws Exception
   {
      queueInputLines("", "n");
      Project project = initializeJavaProject();
      getShell().execute("project add-managed-dependency com.ocpsoft:prettyfaces-jsf2:3.2.0");
      DependencyFacet facet = project.getFacet(DependencyFacet.class);

      DependencyBuilder dep = DependencyBuilder.create();
      dep.setArtifactId("prettyfaces-jsf2");
      dep.setGroupId("com.ocpsoft");
      dep.setVersion("3.0.1");
      dep.setScopeType("compile");
      dep.setPackagingType("jar");

      assertNull(facet.getEffectiveDependency(dep));
      getShell().execute("project add-dependency com.ocpsoft:prettyfaces-jsf2:3.0.1");

      Dependency newDep = facet.getEffectiveDependency(dep);
      dep.setVersion("3.0.1");

      assertEquals(newDep.toCoordinates(), dep.toCoordinates());
   }

   /**
    * Tests overriding managed dependency with a different version.
    * 
    * @throws Exception
    */
   @Test
   public void testAddDependencyOverrideManagedRange() throws Exception
   {
      queueInputLines("", "n", "2");
      Project project = initializeJavaProject();
      getShell().execute("project add-managed-dependency com.ocpsoft:prettyfaces-jsf2:3.2.0");
      DependencyFacet facet = project.getFacet(DependencyFacet.class);

      DependencyBuilder dep = DependencyBuilder.create();
      dep.setArtifactId("prettyfaces-jsf2");
      dep.setGroupId("com.ocpsoft");
      dep.setVersion("3.0.1");
      dep.setScopeType("compile");
      dep.setPackagingType("jar");

      assertNull(facet.getEffectiveDependency(dep));
      getShell().execute("project add-dependency com.ocpsoft:prettyfaces-jsf2");

      Dependency newDep = facet.getEffectiveDependency(dep);
      dep.setVersion("3.0.1");

      assertEquals(newDep.toCoordinates(), dep.toCoordinates());
   }

   @Test
   public void testRemoveFacet() throws Exception
   {
      Project project = initializeJavaProject();
      assertFalse(project.hasFacet(MockFacet.class));
      getShell().execute("project install-facet mockfacet");
      assertTrue(project.hasFacet(MockFacet.class));
      getShell().execute("project remove-facet mockfacet");
      assertFalse(project.hasFacet(MockFacet.class));
   }
}
