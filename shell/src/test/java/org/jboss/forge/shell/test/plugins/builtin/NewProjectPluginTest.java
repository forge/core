/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.forge.shell.test.plugins.builtin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import org.jboss.forge.maven.facets.MavenJavaSourceFacet;
import org.jboss.forge.maven.facets.MavenPackagingFacet;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.MetadataFacet;
import org.jboss.forge.project.packaging.PackagingType;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Test;

/**
 * LsMavenPomPluginTestCase
 * 
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class NewProjectPluginTest extends AbstractShellTest
{
   @Test
   public void testCreateJavaProject() throws Exception
   {
      Shell shell = getShell();
      DirectoryResource origin = shell.getCurrentDirectory();

      initializeJavaProject();
      Project project = getProject();

      DirectoryResource created = shell.getCurrentDirectory();

      assertEquals(created, project.getProjectRoot());
      assertNotSame(origin, created);
   }

   @Test
   public void testCreateProjectWithGroup() throws Exception
   {
      getShell().setCurrentResource(createTempFolder());
      queueInputLines("");
      getShell().execute("new-project --named test --topLevelPackage com.test --type jar");
      Project project = getProject();
      assertEquals("com.test", project.getFacet(MetadataFacet.class).getTopLevelPackage());
      assertEquals("com.test", project.getFacet(MavenJavaSourceFacet.class).getBasePackage());
      assertEquals(PackagingType.JAR, project.getFacet(MavenPackagingFacet.class).getPackagingType());
   }

   @Test
   public void testCreateProjectWithDefaultType() throws Exception
   {
      getShell().setCurrentResource(createTempFolder());
      queueInputLines("");
      getShell().execute("new-project --named test --topLevelPackage com.test");
      Project project = getProject();
      assertEquals("com.test", project.getFacet(MetadataFacet.class).getTopLevelPackage());
      assertEquals("com.test", project.getFacet(MavenJavaSourceFacet.class).getBasePackage());
      assertEquals(PackagingType.JAR, project.getFacet(MavenPackagingFacet.class).getPackagingType());
   }

   @Test
   public void testCreatePomProject() throws Exception
   {
      getShell().setCurrentResource(createTempFolder());
      queueInputLines("");
      getShell().execute("new-project --named test --topLevelPackage com.test --type pom");
      Project project = getProject();
      assertEquals("com.test", project.getFacet(MetadataFacet.class).getTopLevelPackage());
      assertTrue(!project.hasFacet(MavenJavaSourceFacet.class));
      assertEquals(PackagingType.BASIC, project.getFacet(MavenPackagingFacet.class).getPackagingType());
   }

   /**
    * 
    * Tests trying to create a zip (invalid) project, then changing to jar
    * 
    * @throws Exception
    */
   @Test
   public void testTryCreateUnSupportedProject() throws Exception
   {
      getShell().setCurrentResource(createTempFolder());
      queueInputLines("JAR", "y");
      getShell().execute("new-project --named test --topLevelPackage com.test --type zip");
      Project project = getProject();
      assertEquals("com.test", project.getFacet(MetadataFacet.class).getTopLevelPackage());
      assertEquals("com.test", project.getFacet(MavenJavaSourceFacet.class).getBasePackage());
      assertEquals(PackagingType.JAR, project.getFacet(MavenPackagingFacet.class).getPackagingType());
   }

   @Test
   public void testCreateWarProject() throws Exception
   {
      getShell().setCurrentResource(createTempFolder());
      queueInputLines("");
      getShell().execute("new-project --named test --topLevelPackage com.test --type war");
      Project project = getProject();
      assertEquals("com.test", project.getFacet(MetadataFacet.class).getTopLevelPackage());
      assertEquals("com.test", project.getFacet(MavenJavaSourceFacet.class).getBasePackage());
      assertEquals(PackagingType.WAR, project.getFacet(MavenPackagingFacet.class).getPackagingType());
   }

   @Test
   public void testCreateJarProjectWithMain() throws Exception
   {
      getShell().setCurrentResource(createTempFolder());
      queueInputLines("");
      getShell().execute("new-project --named test --topLevelPackage com.test --type jar createMain");
      Project project = getProject();
      assertEquals("com.test", project.getFacet(MetadataFacet.class).getTopLevelPackage());
      assertEquals("com.test", project.getFacet(MavenJavaSourceFacet.class).getBasePackage());
      assertEquals(PackagingType.JAR, project.getFacet(MavenPackagingFacet.class).getPackagingType());
      assertNotNull(project.getFacet(JavaSourceFacet.class).getJavaResource("src/main/java/com/test/Main.java"));
   }

   @Test
   public void testCreatePomProjectWithMain() throws Exception
   {
      getShell().setCurrentResource(createTempFolder());
      queueInputLines("");
      getShell().execute("new-project --named test --topLevelPackage com.test --type pom createMain");
      Project project = getProject();
      assertEquals("com.test", project.getFacet(MetadataFacet.class).getTopLevelPackage());
      assertTrue(!project.hasFacet(MavenJavaSourceFacet.class));
      assertEquals(PackagingType.BASIC, project.getFacet(MavenPackagingFacet.class).getPackagingType());
      assertTrue(!project.hasFacet(JavaSourceFacet.class));
      assertTrue(!project.getProjectRoot().getChildDirectory("src").exists());
   }

   @Test
   public void testCreateProjectBadTopLevelPackage() throws Exception
   {
      getShell().setCurrentResource(createTempFolder());
      queueInputLines("com.test", "");
      getShell().execute("new-project --named test --topLevelPackage com# --type jar");
      Project project = getProject();
      assertEquals("com.test", project.getFacet(MetadataFacet.class).getTopLevelPackage());
      assertEquals("com.test", project.getFacet(MavenJavaSourceFacet.class).getBasePackage());
      assertEquals(PackagingType.JAR, project.getFacet(MavenPackagingFacet.class).getPackagingType());
   }

}
