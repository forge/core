/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.projects.types;

import java.util.ArrayList;
import java.util.List;

import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.projects.ProjectType;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
import org.jboss.forge.addon.projects.facets.PackagingFacet;
import org.jboss.forge.addon.projects.facets.ResourcesFacet;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class ResourcesProjectType implements ProjectType
{
   @Override
   public String getType()
   {
      return "Java Resources";
   }

   @Override
   public Class<? extends UIWizardStep> getSetupFlow()
   {
      return null;
   }

   @Override
   public Iterable<Class<? extends ProjectFacet>> getRequiredFacets()
   {
      List<Class<? extends ProjectFacet>> result = new ArrayList<Class<? extends ProjectFacet>>();
      result.add(MetadataFacet.class);
      result.add(PackagingFacet.class);
      result.add(DependencyFacet.class);
      result.add(ResourcesFacet.class);
      return result;
   }

   @Override
   public String toString()
   {
      return "jar-resources";
   }

   @Override
   public int priority()
   {
      return 1000;
   }

}
