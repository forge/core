/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.maven;

import javax.enterprise.inject.Typed;

import org.jboss.forge.project.BaseProject;
import org.jboss.forge.project.Facet;
import org.jboss.forge.project.facets.FacetNotFoundException;
import org.jboss.forge.project.services.ProjectFactory;
import org.jboss.forge.resources.DirectoryResource;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
@Typed()
public class ProjectImpl extends BaseProject
{
   private DirectoryResource projectRoot = null;
   private final ProjectFactory factory;

   public ProjectImpl(final ProjectFactory factory, final DirectoryResource dir)
   {
      this.factory = factory;
      this.projectRoot = dir;
   }

   @Override
   public <F extends Facet> F getFacet(final Class<F> type)
   {
      try
      {
         return super.getFacet(type);
      }
      catch (FacetNotFoundException e)
      {
         factory.registerSingleFacet(this, type);
         return super.getFacet(type);
      }
   }

   @Override
   public DirectoryResource getProjectRoot()
   {
      return projectRoot;
   }

   @Override
   public boolean exists()
   {
      return (projectRoot != null) && projectRoot.exists();
   }

   @Override
   public String toString()
   {
      return "ProjectImpl [" + getProjectRoot() + "]";
   }
}
