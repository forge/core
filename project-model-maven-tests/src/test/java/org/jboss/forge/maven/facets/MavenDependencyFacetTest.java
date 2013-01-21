/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.maven.facets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.maven.util.ProjectModelTest;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class MavenDependencyFacetTest extends ProjectModelTest
{
   @Deployment
   public static JavaArchive createTestArchive()
   {
      return ProjectModelTest.createTestArchive()
               .addAsManifestResource(
                        "META-INF/services/org.jboss.forge.project.dependencies.DependencyResolverProvider");
   }

   @Test
   public void testHasDependency() throws Exception
   {
      DependencyFacet deps = getProject().getFacet(DependencyFacet.class);

      DependencyBuilder prettyfaces = DependencyBuilder.create("com.ocpsoft:prettyfaces-jsf2:${pf.version}");
      deps.setProperty("pf.version", "3.3.2");
      deps.addDirectDependency(prettyfaces);

      assertTrue(deps.hasDirectDependency(prettyfaces));
      assertTrue(deps.hasEffectiveDependency(prettyfaces));
      assertEquals("3.3.2", deps.getEffectiveDependency(prettyfaces).getVersion());
      assertEquals("3.3.2", deps.getDirectDependency(prettyfaces).getVersion());
   }

   @Test
   public void testHasImportedManagedDependency() throws Exception
   {
      DependencyFacet deps = getProject().getFacet(DependencyFacet.class);
      DependencyBuilder javaeeSpec = DependencyBuilder.create("org.jboss.spec:jboss-javaee-6.0:1.0.0.Final:import:pom");
      assertFalse(deps.hasDirectManagedDependency(javaeeSpec));
      deps.addDirectManagedDependency(javaeeSpec);
      assertTrue(deps.hasDirectManagedDependency(javaeeSpec));

      DependencyBuilder ejb = DependencyBuilder.create("org.jboss.spec.javax.ejb:jboss-ejb-api_3.1_spec:1.0.0.Final");
      assertTrue(deps.hasEffectiveManagedDependency(ejb));
   }

   @Test
   public void testAddDependency() throws Exception
   {
      Dependency dependency =
               DependencyBuilder.create("org.jboss:test-dependency:1.0.0.Final");

      Project project = getProject();
      DependencyFacet deps = project.getFacet(DependencyFacet.class);
      assertFalse(deps.hasEffectiveDependency(dependency));
      deps.addDirectDependency(dependency);
      assertTrue(deps.hasEffectiveDependency(dependency));
      assertTrue(deps.hasDirectDependency(dependency));
   }

   @Test
   public void testRemoveDependency() throws Exception
   {
      Dependency dependency =
               DependencyBuilder.create("org.jboss:test-dependency2:1.0.1.Final");

      Project project = getProject();
      DependencyFacet deps = project.getFacet(DependencyFacet.class);

      assertFalse(deps.hasEffectiveDependency(dependency));
      deps.addDirectDependency(dependency);
      assertTrue(deps.hasDirectDependency(dependency));
      assertTrue(deps.hasEffectiveDependency(dependency));
      deps.removeDependency(dependency);
      assertFalse(deps.hasDirectDependency(dependency));
      assertFalse(deps.hasEffectiveDependency(dependency));
   }

   @Test
   public void testAddProperty() throws Exception
   {
      String version = "1.0.2.Final";
      Project project = getProject();
      DependencyFacet deps = project.getFacet(DependencyFacet.class);
      deps.setProperty("version", version);
      assertEquals(version, deps.getProperty("version"));
   }

    @Test
    public void testAddDependencyUsingProperty() throws Exception {
        DependencyFacet deps = getProject().getFacet(DependencyFacet.class);

        deps.setProperty("jboss.spec.version", "3.0.0.Final");

        DependencyBuilder newDep = DependencyBuilder.create();
        newDep.setGroupId("org.jboss.spec");
        newDep.setArtifactId("jboss-javaee-6.0");
        newDep.setVersion("${jboss.spec.version}");

        deps.addDirectDependency(newDep);

        assertTrue(deps.hasDirectDependency(newDep));
    }

   @Test
   @Ignore
   public void testDoResolveVersions() throws Exception
   {
      Project project = getProject();
      DependencyFacet deps = project.getFacet(DependencyFacet.class);
      List<Dependency> versions = deps.resolveAvailableVersions("com.ocpsoft:prettyfaces-jsf2");
      assertTrue(versions.size() > 4);
   }

   @Test
   public void testHasManagedDependencyImport() throws Exception
   {
      DependencyFacet deps = getProject().getFacet(DependencyFacet.class);

      DependencyBuilder javaeeSpec = DependencyBuilder.create("org.jboss.spec:jboss-javaee-6.0:1.0.0.Final:import:pom");

      assertFalse(deps.hasDirectManagedDependency(javaeeSpec));
      deps.addDirectManagedDependency(javaeeSpec);
      assertTrue(deps.hasDirectManagedDependency(javaeeSpec));
   }

   @Test
   public void testAddManagedDependency() throws Exception
   {
      Dependency dependency =
               DependencyBuilder.create("org.jboss.seam:seam-bom:3.0.0.Final:import:pom");

      Project project = getProject();
      DependencyFacet manDeps = project.getFacet(DependencyFacet.class);
      assertFalse(manDeps.hasDirectManagedDependency(dependency));
      manDeps.addManagedDependency(dependency);
      assertTrue(manDeps.hasDirectManagedDependency(dependency));
   }

   @Test
   public void testRemoveManagedDependency() throws Exception
   {
      Dependency dependency =
               DependencyBuilder.create("org.jboss.seam:seam-bom:3.0.0.Final:import:pom");

      Project project = getProject();
      DependencyFacet deps = project.getFacet(DependencyFacet.class);

      assertFalse(deps.hasDirectManagedDependency(dependency));
      deps.addDirectManagedDependency(dependency);
      assertTrue(deps.hasDirectManagedDependency(dependency));
      deps.removeManagedDependency(dependency);
      assertFalse(deps.hasDirectManagedDependency(dependency));
   }

   @Test
   public void testHasDependencyBehavior() throws Exception
   {
      DependencyFacet dependencyFacet = getProject().getFacet(DependencyFacet.class);
      DependencyBuilder forgeShellApiDependency = DependencyBuilder.create().setGroupId("org.jboss.forge")
               .setArtifactId("forge-shell-api").setVersion("[1.0.0-SNAPSHOT,)");
      DependencyBuilder cdiDependency = DependencyBuilder.create().setGroupId("javax.enterprise")
               .setArtifactId("cdi-api");
      assertFalse(dependencyFacet.hasEffectiveDependency(cdiDependency));
      assertFalse(dependencyFacet.hasDirectDependency(cdiDependency));
      dependencyFacet.addDirectDependency(forgeShellApiDependency);
      assertTrue(dependencyFacet.hasEffectiveDependency(cdiDependency));
      assertFalse(dependencyFacet.hasDirectDependency(cdiDependency));
   }
}
