/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.projects.impl.empty;

import java.util.Collections;

import javax.inject.Inject;

import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectProvider;
import org.jboss.forge.addon.projects.ProvidedProjectFacet;
import org.jboss.forge.addon.resource.Resource;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class EmptyProjectProvider implements ProjectProvider
{
   @Inject
   private FacetFactory facetFactory;

   @Override
   public String getType()
   {
      return "Nothing";
   }

   @Override
   public Iterable<Class<? extends ProvidedProjectFacet>> getProvidedFacetTypes()
   {
      return Collections.<Class<? extends ProvidedProjectFacet>> singleton(EmptyMetadataFacet.class);
   }

   @Override
   public Project createProject(Resource<?> target)
   {
      Project project = new EmptyProject(target);
      EmptyMetadataFacet emptyMetadataFacet = facetFactory.install(project, EmptyMetadataFacet.class);
      emptyMetadataFacet.setProjectProvider(this);
      return project;
   }

   @Override
   public boolean containsProject(Resource<?> resource)
   {
      Resource<?> child = resource.getChild(".project");
      return child.exists();
   }

   @Override
   public int priority()
   {
      return Integer.MAX_VALUE;
   }

}
