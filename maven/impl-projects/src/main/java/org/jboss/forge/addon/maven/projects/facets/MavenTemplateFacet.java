/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.projects.facets;

import java.io.File;

import org.jboss.forge.addon.facets.AbstractFacet;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.maven.projects.MavenFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.templates.facets.TemplateFacet;

/**
 * An implementation of the {@link org.jboss.forge.addon.templates.facets.TemplateFacet} for Maven projects.
 * 
 * @author Vineet Reynolds
 */
@FacetConstraint(MavenFacet.class)
public class MavenTemplateFacet extends AbstractFacet<Project>implements TemplateFacet
{

   @Override
   public DirectoryResource getRootTemplateDirectory()
   {
      Project project = getFaceted();
      return project.getRoot().reify(DirectoryResource.class).getChildDirectory("src" + File.separator + "main"
               + File.separator + "templates");
   }

   @Override
   public boolean isInstalled()
   {
      Project project = getFaceted();
      return project.hasFacet(MavenFacet.class) && getRootTemplateDirectory().exists();
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
   public FileResource<?> getResource(final String path)
   {
      return (FileResource<?>) getRootTemplateDirectory().getChild(path);
   }

}
