/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.projects.facets;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;

import org.apache.maven.model.Build;
import org.jboss.forge.addon.facets.AbstractFacet;
import org.jboss.forge.addon.maven.projects.MavenFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.facets.ResourcesFacet;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;

/**
 * Handles Maven Resource folders
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
@Dependent
public class MavenResourceFacet extends AbstractFacet<Project> implements ResourcesFacet
{
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
      MavenFacet mavenFacet = getFaceted().getFacet(MavenFacet.class);
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
      DirectoryResource projectRoot = getFaceted().getProjectRoot();
      return projectRoot.getChildDirectory(resFolderName);
   }

   @Override
   public DirectoryResource getTestResourceFolder()
   {
      MavenFacet mavenFacet = getFaceted().getFacet(MavenFacet.class);
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
      DirectoryResource projectRoot = getFaceted().getProjectRoot();
      return projectRoot.getChildDirectory(resFolderName);
   }

   @Override
   public void setFaceted(Project origin)
   {
      super.setFaceted(origin);
   }

   @Override
   public boolean isInstalled()
   {
      return getFaceted().hasFacet(MavenFacet.class) && getResourceFolder().exists();
   }

   @Override
   public boolean install()
   {
      if (!this.isInstalled())
      {
         for (DirectoryResource folder : getResourceFolders())
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
