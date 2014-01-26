/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.projects.mock;

import java.util.Arrays;

import org.jboss.forge.addon.projects.BuildSystem;
import org.jboss.forge.addon.projects.BuildSystemFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
import org.jboss.forge.addon.resource.DirectoryResource;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class MockBuildSystem implements BuildSystem
{

   @Override
   public String getType()
   {
      return "buildsystem";
   }

   @Override
   public Iterable<Class<? extends BuildSystemFacet>> getProvidedFacetTypes()
   {
      return Arrays.<Class<? extends BuildSystemFacet>> asList(MetadataFacet.class);
   }

   @Override
   public Project createProject(DirectoryResource targetDir)
   {
      return new MockProject(targetDir);
   }

   @Override
   public boolean containsProject(DirectoryResource resource)
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
