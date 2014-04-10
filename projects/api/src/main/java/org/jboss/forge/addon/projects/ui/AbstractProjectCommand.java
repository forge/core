/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.projects.ui;

import java.util.Set;

import org.jboss.forge.addon.facets.constraints.FacetConstraints;
import org.jboss.forge.addon.facets.constraints.FacetInspector;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.Projects;
import org.jboss.forge.addon.ui.command.AbstractUICommand;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIContextProvider;
import org.jboss.forge.addon.ui.context.UISelection;

/**
 * Supports {@link Project} specific operations on {@link UICommand} sub-classes. Also enables the use of
 * {@link FacetConstraints} on implementations for which {@link AbstractProjectCommand#isProjectRequired()} returns
 * <code>true</code>.
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public abstract class AbstractProjectCommand extends AbstractUICommand
{
   @Override
   public boolean isEnabled(UIContext context)
   {
      return ((isProjectRequired() && containsProject(context) && constraintsSatisfied(context)) && super
               .isEnabled(context)) || (!isProjectRequired() && super.isEnabled(context));
   }

   private boolean constraintsSatisfied(UIContext context)
   {
      Set<Class<ProjectFacet>> facets = FacetInspector.getRequiredFacets(getMetadata(context).getType());
      return FacetInspector.isConstraintSatisfied(getSelectedProject(context), facets);
   }

   /**
    * Implementations should return <code>true</code> if a {@link Project} is required in order for this command to
    * execute. If <code>false</code>, this command will run without a {@link Project} selected in the current
    * {@link UIContext}. Returning <code>true</code> also enables the use of {@link FacetConstraints} on the
    * implementation {@link Class} declaration.
    */
   protected abstract boolean isProjectRequired();

   /**
    * Returns <code>true</code> if a {@link Project} exists in the current {@link UISelection}.
    */
   protected boolean containsProject(UIContext context)
   {
      return Projects.containsProject(getProjectFactory(), context);
   }

   /**
    * Returns the selected {@link Project}. <code>null</code> if no {@link Project} is found.
    */
   protected Project getSelectedProject(UIContextProvider contextProvider)
   {
      return getSelectedProject(contextProvider.getUIContext());
   }

   /**
    * Returns the selected {@link Project}. <code>null</code> if no {@link Project} is found.
    */
   protected Project getSelectedProject(UIContext context)
   {
      return Projects.getSelectedProject(getProjectFactory(), context);
   }

   /**
    * Returns a reference to the {@link ProjectFactory}.
    */
   protected abstract ProjectFactory getProjectFactory();

}
