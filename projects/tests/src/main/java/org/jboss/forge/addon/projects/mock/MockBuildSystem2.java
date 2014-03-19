/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.projects.mock;

import java.util.Arrays;

import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectProvider;
import org.jboss.forge.addon.projects.ProvidedProjectFacet;
import org.jboss.forge.addon.projects.facets.PackagingFacet;
import org.jboss.forge.addon.resource.Resource;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class MockBuildSystem2 implements ProjectProvider
{

   @Override
   public String getType()
   {
      return "buildsystem2";
   }

   @Override
   public Iterable<Class<? extends ProvidedProjectFacet>> getProvidedFacetTypes()
   {
      return Arrays.<Class<? extends ProvidedProjectFacet>> asList(PackagingFacet.class);
   }

   @Override
   public Project createProject(Resource<?> target)
   {
      return null;
   }

   @Override
   public boolean containsProject(Resource<?> resource)
   {
      return false;
   }

   @Override
   public String toString()
   {
      return getType();
   }

   @Override
   public int priority()
   {
      return 0;
   }

}
