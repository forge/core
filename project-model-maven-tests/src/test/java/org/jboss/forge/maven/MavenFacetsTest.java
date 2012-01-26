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

package org.jboss.forge.maven;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.maven.util.ProjectModelTest;
import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.services.ProjectFactory;
import org.jboss.forge.project.services.ResourceFactory;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.shell.util.ResourceUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Singleton
@RunWith(Arquillian.class)
public class MavenFacetsTest extends ProjectModelTest
{
   private static final String PKG = MavenFacetsTest.class.getSimpleName().toLowerCase();

   @Inject
   private ProjectFactory projectFactory;

   @Inject
   private ResourceFactory resourceFactory;

   private static Project thisProject;
   private static Project testProject;

   @Before
   @Override
   public void before() throws IOException
   {
      super.before();

      if (thisProject == null)
      {
         thisProject = projectFactory.findProjectRecursively(ResourceUtil.getContextDirectory(resourceFactory
                  .getResourceFrom(new File(""))));
      }
      if (testProject == null)
      {
         testProject = projectFactory.findProjectRecursively(ResourceUtil.getContextDirectory(resourceFactory
                  .getResourceFrom(new File("src/test/resources/test-pom"))));
      }
   }

   @Test
   public void testCreateDefault() throws Exception
   {
      assertTrue(getProject().exists());
   }

   @Test
   public void testGetDefaultSourceDir() throws Exception
   {
      Resource<?> expected = getProject().getProjectRoot().getChild("/src/main/java/");
      DirectoryResource actual = getProject().getFacet(JavaSourceFacet.class).getSourceFolder();
      assertEquals(expected, actual);
   }

   @Test
   public void testGetTestSourceDir() throws Exception
   {
      Resource<?> expected = getProject().getProjectRoot().getChild("/src/test/java/");
      DirectoryResource actual = getProject().getFacet(JavaSourceFacet.class).getTestSourceFolder();
      assertEquals(expected, actual);
   }

   @Test
   public void testCreateJavaFile() throws Exception
   {
      String name = "JustCreated";
      JavaClass clazz = JavaParser.create(JavaClass.class).setName(name).setPackage(PKG);
      clazz.getOrigin();
      JavaResource file = getProject().getFacet(JavaSourceFacet.class).saveJavaSource(clazz);
      assertEquals(name + ".java", file.getName());

      JavaClass result = (JavaClass) file.getJavaSource();
      assertEquals(name, result.getName());
      assertEquals(PKG, result.getPackage());
      assertTrue(file.delete());
      assertEquals(clazz, result);
   }

   @Test
   public void testCreatePOM() throws Exception
   {
      Model pom = getProject().getFacet(MavenCoreFacet.class).getPOM();
      pom.setGroupId("org.jboss.forge.generated");
      pom.setArtifactId("generated-pom");
      pom.setVersion("X-SNAPSHOT");
      getProject().getFacet(MavenCoreFacet.class).setPOM(pom);
      File file = pom.getPomFile();
      assertTrue(file.exists());

      MavenXpp3Reader reader = new MavenXpp3Reader();
      Model result = reader.read(new FileInputStream(file));
      assertEquals(pom.getArtifactId(), result.getArtifactId());
   }

   @Test
   public void testProjectIsCurrentProject() throws Exception
   {
      Model pom = thisProject.getFacet(MavenCoreFacet.class).getPOM();
      assertEquals("forge-project-model-maven-tests", pom.getArtifactId());
   }

   @Test
   public void testAbsoluteProjectIsResolvedCorrectly() throws Exception
   {
      MavenCoreFacet maven = testProject.getFacet(MavenCoreFacet.class);
      Model pom = maven.getPOM();
      assertEquals("socialpm", pom.getArtifactId());
   }

   @Test
   public void testResolveProperties() throws Exception
   {
      MavenCoreFacet maven = testProject.getFacet(MavenCoreFacet.class);
      assertEquals("4.8.1", maven.resolveProperties("${junit.version}"));
   }

   @Test
   public void testAbsoluteUnknownProjectCannotInstantiate() throws Exception
   {
      DirectoryResource temp = new DirectoryResource(resourceFactory, File.createTempFile(PKG, null));
      temp.delete(true);
      temp.mkdirs();
      Project project = projectFactory.findProjectRecursively(temp);
      assertNull(project);
   }

   @Test
   @SuppressWarnings("unchecked")
   public void testAbsoluteUnknownProjectInstantiatesWithCreateTrue() throws Exception
   {
      DirectoryResource temp = new DirectoryResource(resourceFactory, File.createTempFile(PKG, null));
      temp.delete(true);
      temp.mkdirs();
      assertNotNull(projectFactory.createProject(temp, MavenCoreFacet.class, JavaSourceFacet.class));
      assertNotNull(projectFactory.findProjectRecursively(temp));
   }
}
