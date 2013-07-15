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
import org.jboss.forge.project.packaging.PackagingType;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Assert;
import org.junit.Test;

public class MavenWebResourceFacetTest extends AbstractShellTest
{
   @Test
   public void testDefaultWebappFolder() throws Exception
   {
      Project project = initializeProject(PackagingType.WAR);
      MavenWebResourceFacet facet = project.getFacet(MavenWebResourceFacet.class);
      DirectoryResource expected = project.getProjectRoot().getChildDirectory(
               "src" + File.separator + "main" + File.separator + "webapp");
      Assert.assertEquals(expected.getFullyQualifiedName(), facet.getWebRootDirectory().getFullyQualifiedName());
   }

   @Test
   public void testCustomWebappFolder() throws Exception
   {
      Project project = initializeProject(PackagingType.WAR);

      MavenPomResource pom = project.getProjectRoot().getChild("pom.xml").reify(MavenPomResource.class);

      pom.setContents("<project><modelVersion>4.0.0.</modelVersion><groupId>com.test</groupId><artifactId>testme</artifactId><version>1.0</version><build><plugins><plugin><artifactId>maven-war-plugin</artifactId><version>2.4</version><configuration><warSourceDirectory>WebContent</warSourceDirectory><failOnMissingWebXml>false</failOnMissingWebXml></configuration></plugin></plugins></build></project>");

      MavenWebResourceFacet facet = project.getFacet(MavenWebResourceFacet.class);
      DirectoryResource expected = project.getProjectRoot().getChildDirectory(
               "WebContent");
      Assert.assertEquals(expected.getFullyQualifiedName(), facet.getWebRootDirectory().getFullyQualifiedName());
   }
}
