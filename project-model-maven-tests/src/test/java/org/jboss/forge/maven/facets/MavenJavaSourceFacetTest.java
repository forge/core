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

public class MavenJavaSourceFacetTest extends AbstractShellTest
{
   @Test
   public void testDefaultSourceFolder() throws Exception
   {
      Project project = initializeJavaProject();
      MavenJavaSourceFacet facet = project.getFacet(MavenJavaSourceFacet.class);
      DirectoryResource expected = project.getProjectRoot().getChildDirectory(
               "src" + File.separator + "main" + File.separator + "java");
      Assert.assertEquals(expected.getFullyQualifiedName(), facet.getSourceFolder().getFullyQualifiedName());
   }

   @Test
   public void testDefaultTestSourceFolder() throws Exception
   {
      Project project = initializeJavaProject();
      MavenJavaSourceFacet facet = project.getFacet(MavenJavaSourceFacet.class);
      DirectoryResource expected = project.getProjectRoot().getChildDirectory(
               "src" + File.separator + "test" + File.separator + "java");
      Assert.assertEquals(expected.getFullyQualifiedName(), facet.getTestSourceFolder().getFullyQualifiedName());
   }

   @Test
   public void testCustomSourceFolder() throws Exception
   {
      Project project = initializeJavaProject();

      MavenPomResource pom = project.getProjectRoot().getChild("pom.xml").reify(MavenPomResource.class);

      pom.setContents("<project><modelVersion>4.0.0.</modelVersion><groupId>com.test</groupId><artifactId>testme</artifactId><version>1.0</version><build><sourceDirectory>src</sourceDirectory></build></project>");

      MavenJavaSourceFacet facet = project.getFacet(MavenJavaSourceFacet.class);
      DirectoryResource expected = project.getProjectRoot().getChildDirectory(
               "src");
      Assert.assertEquals(expected.getFullyQualifiedName(), facet.getSourceFolder().getFullyQualifiedName());
   }

   @Test
   public void testCustomTestSourceFolder() throws Exception
   {
      Project project = initializeJavaProject();

      MavenPomResource pom = project.getProjectRoot().getChild("pom.xml").reify(MavenPomResource.class);

      pom.setContents("<project><modelVersion>4.0.0.</modelVersion><groupId>com.test</groupId><artifactId>testme</artifactId><version>1.0</version><build><testSourceDirectory>test</testSourceDirectory></build></project>");

      MavenJavaSourceFacet facet = project.getFacet(MavenJavaSourceFacet.class);
      DirectoryResource expected = project.getProjectRoot().getChildDirectory(
               "test");
      Assert.assertEquals(expected.getFullyQualifiedName(), facet.getTestSourceFolder().getFullyQualifiedName());
   }
}
