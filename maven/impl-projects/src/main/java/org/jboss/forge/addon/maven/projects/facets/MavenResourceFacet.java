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

import org.jboss.forge.addon.facets.AbstractFacet;
import org.jboss.forge.addon.maven.projects.MavenFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.facets.ResourceFacet;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Dependent
public class MavenResourceFacet extends AbstractFacet<Project> implements ResourceFacet
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
      return getFaceted().getProjectRoot().getChildDirectory("src" + File.separator + "main"
               + File.separator + "resources");
   }

   @Override
   public DirectoryResource getTestResourceFolder()
   {
      return getFaceted().getProjectRoot().getChildDirectory("src" + File.separator + "test"
               + File.separator + "resources");
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
