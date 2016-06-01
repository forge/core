/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.projects.types;

import java.util.LinkedList;
import java.util.List;

import org.jboss.forge.addon.projects.AbstractProjectType;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
import org.jboss.forge.addon.projects.facets.ModuleFacet;
import org.jboss.forge.addon.projects.facets.PackagingFacet;
import org.jboss.forge.addon.projects.stacks.Stack;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;

/**
 * Creates a project to be used as a parent of other projects
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ParentProjectType extends AbstractProjectType
{
   @Override
   public String getType()
   {
      return "Parent";
   }

   @Override
   public Class<? extends UIWizardStep> getSetupFlow()
   {
      return null;
   }

   @Override
   public Iterable<Class<? extends ProjectFacet>> getRequiredFacets()
   {
      List<Class<? extends ProjectFacet>> result = new LinkedList<>();
      result.add(MetadataFacet.class);
      result.add(PackagingFacet.class);
      result.add(DependencyFacet.class);
      result.add(ModuleFacet.class);
      return result;
   }

   @Override
   public int priority()
   {
      return 300;
   }

   @Override
   public String toString()
   {
      return "parent";
   }

   @Override
   public boolean supports(Stack stack)
   {
      return false;
   }
}
