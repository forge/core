/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.maven.facets;

import java.io.File;

import org.jboss.forge.maven.resources.MavenPomResource;
import org.jboss.forge.project.Project;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Assert;
import org.junit.Test;

public class MavenResourceFacetTest extends AbstractShellTest
{
   @Test
   public void testDefaultResourceFolder() throws Exception
   {
      Project project = initializeJavaProject();
      MavenResourceFacet facet = project.getFacet(MavenResourceFacet.class);
      DirectoryResource expected = project.getProjectRoot().getChildDirectory(
               "src" + File.separator + "main" + File.separator + "resources");
      Assert.assertEquals(expected.getFullyQualifiedName(), facet.getResourceFolder().getFullyQualifiedName());
   }

   @Test
   public void testDefaultTestResourceFolder() throws Exception
   {
      Project project = initializeJavaProject();
      MavenResourceFacet facet = project.getFacet(MavenResourceFacet.class);
      DirectoryResource expected = project.getProjectRoot().getChildDirectory(
               "src" + File.separator + "test" + File.separator + "resources");
      Assert.assertEquals(expected.getFullyQualifiedName(), facet.getTestResourceFolder().getFullyQualifiedName());
   }

   @Test
   public void testCustomResourceFolder() throws Exception
   {
      Project project = initializeJavaProject();

      MavenPomResource pom = project.getProjectRoot().getChild("pom.xml").reify(MavenPomResource.class);

      pom.setContents("<project><modelVersion>4.0.0.</modelVersion><groupId>com.test</groupId><artifactId>testme</artifactId><version>1.0</version><build><resources><resource><directory>foo</directory></resource></resources></build></project>");

      MavenResourceFacet facet = project.getFacet(MavenResourceFacet.class);
      DirectoryResource expected = project.getProjectRoot().getChildDirectory(
               "foo");
      Assert.assertEquals(expected.getFullyQualifiedName(), facet.getResourceFolder().getFullyQualifiedName());
   }

   @Test
   public void testCustomTestSourceFolder() throws Exception
   {
      Project project = initializeJavaProject();

      MavenPomResource pom = project.getProjectRoot().getChild("pom.xml").reify(MavenPomResource.class);

      pom.setContents("<project><modelVersion>4.0.0.</modelVersion><groupId>com.test</groupId><artifactId>testme</artifactId><version>1.0</version><build><testResources><testResource><directory>foo</directory></testResource></testResources></build></project>");

      MavenResourceFacet facet = project.getFacet(MavenResourceFacet.class);
      DirectoryResource expected = project.getProjectRoot().getChildDirectory(
               "foo");
      Assert.assertEquals(expected.getFullyQualifiedName(), facet.getTestResourceFolder().getFullyQualifiedName());
   }
}
