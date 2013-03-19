/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.scaffoldx.facets;

import java.io.File;
import java.io.InputStream;

import javax.enterprise.context.Dependent;

import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.project.Facet;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.project.facets.FacetNotFoundException;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.RequiresFacet;

@Dependent
@Alias("forge.maven.scaffold.TemplateFacet")
@RequiresFacet(MavenCoreFacet.class)
public class MavenScaffoldTemplateFacet extends BaseFacet implements ScaffoldTemplateFacet, Facet
{
   private Project project;

   @Override
   public DirectoryResource getRootTemplateDirectory()
   {
      return project.getProjectRoot().getChildDirectory("src" + File.separator + "main"
               + File.separator + "templates");
   }
   
   @Override
   public DirectoryResource getTemplateDirectory(String provider)
   {
      return this.getRootTemplateDirectory().getChildDirectory(provider);
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
         return getRootTemplateDirectory().exists();
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
          this.getRootTemplateDirectory().mkdir();
      }
      return true;
   }

   @Override
   public FileResource<?> getResource(final String provider, final String relativePath)
   {
      return (FileResource<?>) getTemplateDirectory(provider).getChild(relativePath);
   }

   @Override
   public FileResource<?> createResource(InputStream data, final String provider, final String relativeFilename)
   {
      FileResource<?> file = (FileResource<?>) getTemplateDirectory(provider).getChild(relativeFilename);
      file.setContents(data);
      return file;
   }
   
   @Override
   public FileResource<?> createResource(final char[] data, final String provider, final String relativeFilename)
   {
      FileResource<?> file = (FileResource<?>) getTemplateDirectory(provider).getChild(relativeFilename);
      file.setContents(data);
      return file;
   }
   
   @Override
   public FileResource<?> createResource(String data, final String provider, final String relativeFilename)
   {
      FileResource<?> file = (FileResource<?>) getTemplateDirectory(provider).getChild(relativeFilename);
      file.setContents(data);
      return file;
   }

}
