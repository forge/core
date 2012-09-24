/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.maven.providers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.maven.model.Model;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.packaging.PackagingType;
import org.jboss.forge.project.services.ProjectFactory;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:torben@jit-central.com">Torben Jaeger</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class MavenMultiModuleProviderTest extends AbstractShellTest
{

   @Inject
   ProjectFactory factory;

   @Test
   public void testSubmoduleProject() throws Exception
   {
      Map<String, Object> children = new HashMap<String, Object>(2);
      children.put("child1", null);
      children.put("child2", null);

      // Parent project, type POM
      Project pomPrj = initializeProject(PackagingType.NONE);
      DirectoryResource rootDir = pomPrj.getProjectRoot();

      // Child project, type JAR
      getShell().setCurrentResource(pomPrj.getProjectRoot());
      queueInputLines("", "");
      getShell().execute("new-project --named child1 --topLevelPackage com.test --type JAR");

      Model pom = pomPrj.getFacet(MavenCoreFacet.class).getPOM();
      assertEquals("wrong pom", "child1", pom.getArtifactId());
      assertNotNull("parent is null", pom.getParent());
      assertEquals("wrong parent", "test", pom.getParent().getArtifactId());

      Project parentPrj = factory.findProject(rootDir);
      pom = parentPrj.getFacet(MavenCoreFacet.class).getPOM();

      assertEquals("wrong pom", "test", pom.getArtifactId());

      List<String> modules = pom.getModules();
      assertTrue(1 == modules.size());
      for (String module : modules) {
         assertEquals("wrong module", "child1", module);
      }

      // Child project, type WAR
      getShell().setCurrentResource(rootDir);
      queueInputLines("", "");
      getShell().execute("new-project --named child2 --topLevelPackage com.test --type WAR");
      pom = pomPrj.getFacet(MavenCoreFacet.class).getPOM();
      assertEquals("wrong pom", "child2", pom.getArtifactId());
      assertNotNull("parent is null", pom.getParent());
      assertEquals("wrong parent", "test", pom.getParent().getArtifactId());

      parentPrj = factory.findProject(rootDir);
      pom = parentPrj.getFacet(MavenCoreFacet.class).getPOM();
      assertEquals("wrong pom", "test", pom.getArtifactId());
      for (String module : pom.getModules()) {
         children.remove(module);
      }
      assertTrue("modules remaining ...", 0 == children.size());

   }
}
