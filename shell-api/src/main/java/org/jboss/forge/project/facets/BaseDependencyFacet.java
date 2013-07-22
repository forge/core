/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.project.facets;

import javax.inject.Inject;

import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyInstaller;

/**
 * A base facet class for facets that require a dependency in order to install
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public abstract class BaseDependencyFacet extends BaseFacet
{

   @Inject
   private DependencyInstaller installer;

   @Override
   public boolean install()
   {
      DependencyFacet facet = getProject().getFacet(DependencyFacet.class);
      if (!isInstalled())
      {
         for (Dependency requirement : getRequiredFacetDependencies())
         {
            Dependency choice = installer.install(getProject(), requirement);
            if (!facet.hasEffectiveDependency(requirement))
            {
               installer.install(getProject(), choice);
            }
         }
      }
      return true;
   }

   @Override
   public boolean isInstalled()
   {
      DependencyFacet facet = getProject().getFacet(DependencyFacet.class);
      for (Dependency requirement : getRequiredFacetDependencies())
      {
         if (!facet.hasEffectiveDependency(requirement))
         {
            return false;
         }
      }
      return true;
   }

   protected abstract Iterable<Dependency> getRequiredFacetDependencies();
}
