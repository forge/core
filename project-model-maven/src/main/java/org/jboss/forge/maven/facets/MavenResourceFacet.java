/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.maven.facets;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;

import org.apache.maven.model.Build;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.project.Facet;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.project.facets.FacetNotFoundException;
import org.jboss.forge.project.facets.ResourceFacet;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.RequiresFacet;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Dependent
@Alias("forge.maven.ResourceFacet")
@RequiresFacet(MavenCoreFacet.class)
public class MavenResourceFacet extends BaseFacet implements ResourceFacet, Facet
{
   private Project project;

   @Override
   public List<DirectoryResource> getResourceFolders()
   {
      List<DirectoryResource> result = new ArrayList<DirectoryResource>();
      result.add(getResourceFolder());
      result.add(getTestResourceFolder());
      return result;
   }

   @Override
   public DirectoryResource getResourceFolder()
   {
      MavenCoreFacet mavenFacet = project.getFacet(MavenCoreFacet.class);
      Build build = mavenFacet.getPOM().getBuild();
      final String resFolderName;
      if (build != null && !build.getResources().isEmpty() && build.getResources().get(0).getDirectory() != null)
      {
         resFolderName = build.getResources().get(0).getDirectory();
      }
      else
      {
         resFolderName = "src" + File.separator + "main" + File.separator + "resources";
      }
      DirectoryResource projectRoot = project.getProjectRoot();
      return projectRoot.getChildDirectory(resFolderName);
   }

   @Override
   public DirectoryResource getTestResourceFolder()
   {
      MavenCoreFacet mavenFacet = project.getFacet(MavenCoreFacet.class);
      Build build = mavenFacet.getPOM().getBuild();
      final String resFolderName;
      if (build != null && !build.getTestResources().isEmpty()
               && build.getTestResources().get(0).getDirectory() != null)
      {
         resFolderName = build.getTestResources().get(0).getDirectory();
      }
      else
      {
         resFolderName = "src" + File.separator + "test" + File.separator + "resources";
      }
      DirectoryResource projectRoot = project.getProjectRoot();
      return projectRoot.getChildDirectory(resFolderName);
   }

   @Override
   public Project getProject()
   {
      return project;
   }

   @Override
   public void setProject(final Project project)
   {
      this.project = project;
   }

   @Override
   public boolean isInstalled()
   {
      try
      {
         project.getFacet(MavenCoreFacet.class);
         return getResourceFolder().exists();
      }
      catch (FacetNotFoundException e)
      {
         return false;
      }
   }

   @Override
   public boolean install()
   {
      if (!this.isInstalled())
      {
         for (DirectoryResource folder : this.getResourceFolders())
         {
            folder.mkdirs();
         }
      }
      return true;
   }

   @Override
   public FileResource<?> getResource(final String relativePath)
   {
      return (FileResource<?>) getResourceFolder().getChild(relativePath);
   }

   @Override
   public FileResource<?> getTestResource(final String relativePath)
   {
      return (FileResource<?>) getTestResourceFolder().getChild(relativePath);
   }

   @Override
   public FileResource<?> createResource(final char[] bytes, final String relativeFilename)
   {
      FileResource<?> file = (FileResource<?>) getResourceFolder().getChild(relativeFilename);
      file.setContents(bytes);
      return file;
   }

   @Override
   public FileResource<?> createTestResource(final char[] bytes, final String relativeFilename)
   {
      FileResource<?> file = (FileResource<?>) getTestResourceFolder().getChild(relativeFilename);
      file.setContents(bytes);
      return file;
   }
}
