/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.faces;

import java.io.File;

import org.jboss.forge.addon.javaee.AbstractJavaEEFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.dependencies.DependencyInstaller;
import org.jboss.forge.addon.projects.facets.PackagingFacet;
import org.jboss.forge.addon.projects.facets.ResourcesFacet;
import org.jboss.forge.addon.projects.facets.WebResourcesFacet;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;

/**
 * Common Implementation for all JSF versions
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public abstract class AbstractFacesFacetImpl extends AbstractJavaEEFacet implements FacesFacet
{
   public AbstractFacesFacetImpl(DependencyInstaller installer)
   {
      super(installer);
   }

   @Override
   public FileResource<?> getConfigFile()
   {
      Project project = getFaceted();
      PackagingFacet packaging = project.getFacet(PackagingFacet.class);
      if ("war".equals(packaging.getPackagingType()))
      {
         DirectoryResource webRoot = project.getFacet(WebResourcesFacet.class).getWebRootDirectory();
         return (FileResource<?>) webRoot.getChild("WEB-INF" + File.separator + "faces-config.xml");
      }
      else
      {
         DirectoryResource root = project.getFacet(ResourcesFacet.class).getResourceFolder();
         return (FileResource<?>) root.getChild("META-INF" + File.separator + "faces-config.xml");
      }
   }

}
