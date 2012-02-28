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

package org.jboss.forge.maven.util;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.forge.Root;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.maven.MavenFacetsTest;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.PackagingFacet;
import org.jboss.forge.project.facets.ResourceFacet;
import org.jboss.forge.project.facets.WebResourceFacet;
import org.jboss.forge.project.services.ProjectFactory;
import org.jboss.forge.project.services.ResourceFactory;
import org.jboss.forge.shell.util.ResourceUtil;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ByteArrayAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Before;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class ProjectModelTest
{

   @Deployment
   public static JavaArchive createTestArchive()
   {
      return ShrinkWrap.create(JavaArchive.class, "test.jar")
               .addPackages(true, Root.class.getPackage())
               .addClass(ResourceFactory.class)
               .addAsManifestResource(new ByteArrayAsset("<beans/>".getBytes()), ArchivePaths.create("beans.xml"))
               .addAsManifestResource("META-INF/services/javax.enterprise.inject.spi.Extension");
   }

   private static final String PKG = MavenFacetsTest.class.getSimpleName().toLowerCase();
   private static File tempFolder;

   @Inject
   private ProjectFactory projectFactory;

   @Inject
   private ResourceFactory resourceFactory;

   protected static Project project;

   @Before
   @SuppressWarnings("unchecked")
   public void before() throws IOException
   {
      if (project == null)
      {
         tempFolder = File.createTempFile(PKG, null);
         tempFolder.delete();
         tempFolder.mkdirs();

         project = projectFactory.createProject(
                  ResourceUtil.getContextDirectory(resourceFactory.getResourceFrom(tempFolder)),
                  MavenCoreFacet.class, JavaSourceFacet.class, ResourceFacet.class, WebResourceFacet.class,
                  DependencyFacet.class, PackagingFacet.class);
      }
   }

   @After
   public void after()
   {
      project.getProjectRoot().delete(true);
      project = null;
   }

   protected Project getProject()
   {
      return project;
   }

}
