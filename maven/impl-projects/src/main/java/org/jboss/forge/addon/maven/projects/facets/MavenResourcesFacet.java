/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.projects.facets;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Build;
import org.jboss.forge.addon.facets.AbstractFacet;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.maven.projects.MavenFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.facets.ResourcesFacet;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFilter;
import org.jboss.forge.addon.resource.visit.ResourceVisit;
import org.jboss.forge.addon.resource.visit.ResourceVisitor;

/**
 * Handles Maven Resource folders
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
@FacetConstraint(MavenFacet.class)
public class MavenResourcesFacet extends AbstractFacet<Project>implements ResourcesFacet
{
   @Override
   public List<DirectoryResource> getResourceDirectories()
   {
      List<DirectoryResource> result = new ArrayList<>();
      result.add(getResourceDirectory());
      result.add(getTestResourceDirectory());
      return result;
   }

   @Override
   public DirectoryResource getResourceDirectory()
   {
      MavenFacet mavenFacet = getFaceted().getFacet(MavenFacet.class);
      Build build = mavenFacet.getModel().getBuild();
      final String resFolderName;
      if (build != null && !build.getResources().isEmpty() && build.getResources().get(0).getDirectory() != null)
      {
         resFolderName = build.getResources().get(0).getDirectory();
      }
      else
      {
         resFolderName = "src" + File.separator + "main" + File.separator + "resources";
      }
      DirectoryResource projectRoot = getFaceted().getRoot().reify(DirectoryResource.class);
      return projectRoot.getChildDirectory(resFolderName);
   }

   @Override
   public DirectoryResource getTestResourceDirectory()
   {
      MavenFacet mavenFacet = getFaceted().getFacet(MavenFacet.class);
      Build build = mavenFacet.getModel().getBuild();
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
      DirectoryResource projectRoot = getFaceted().getRoot().reify(DirectoryResource.class);
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
      return getResourceDirectory().exists();
   }

   @Override
   public boolean install()
   {
      if (!this.isInstalled())
      {
         for (DirectoryResource folder : getResourceDirectories())
         {
            folder.mkdirs();
         }
      }
      return true;
   }

   @Override
   public FileResource<?> getResource(final String relativePath)
   {
      return (FileResource<?>) getResourceDirectory().getChild(relativePath);
   }

   @Override
   public FileResource<?> getTestResource(final String relativePath)
   {
      return (FileResource<?>) getTestResourceDirectory().getChild(relativePath);
   }

   @Override
   public FileResource<?> createResource(final char[] bytes, final String relativeFilename)
   {
      FileResource<?> file = (FileResource<?>) getResourceDirectory().getChild(relativeFilename);
      file.setContents(bytes);
      return file;
   }

   @Override
   public FileResource<?> createTestResource(final char[] bytes, final String relativeFilename)
   {
      FileResource<?> file = (FileResource<?>) getTestResourceDirectory().getChild(relativeFilename);
      file.setContents(bytes);
      return file;
   }

   @Override
   public void visitResources(ResourceVisitor visitor)
   {
      new ResourceVisit(getResourceDirectory()).perform(visitor, new ResourceFilter()
      {
         @Override
         public boolean accept(Resource<?> resource)
         {
            return resource instanceof DirectoryResource;
         }
      }, new ResourceFilter()
      {
         @Override
         public boolean accept(Resource<?> type)
         {
            return true;
         }
      });
   }

   @Override
   public void visitTestResources(ResourceVisitor visitor)
   {
      new ResourceVisit(getTestResourceDirectory()).perform(visitor, new ResourceFilter()
      {
         @Override
         public boolean accept(Resource<?> resource)
         {
            return resource instanceof DirectoryResource;
         }
      }, new ResourceFilter()
      {
         @Override
         public boolean accept(Resource<?> type)
         {
            return true;
         }
      });
   }
}
