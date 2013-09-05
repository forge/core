/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.projects.ui;

import java.util.Set;

import javax.inject.Inject;

import org.jboss.forge.addon.facets.constraints.FacetConstraints;
import org.jboss.forge.addon.facets.constraints.FacetInspector;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.ui.AbstractUICommand;
import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.context.UIContext;
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
   @Inject
   private ProjectFactory projectFactory;

   @Override
   public boolean isEnabled(UIContext context)
   {
      return ((isProjectRequired() && containsProject(context) && constraintsSatisfied(context)) && super
               .isEnabled(context)) || (!isProjectRequired() && super.isEnabled(context));
   }

   private boolean constraintsSatisfied(UIContext context)
   {
      Set<Class<ProjectFacet>> facets = FacetInspector.getRequiredFacets(getMetadata().getType());
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
      UISelection<FileResource<?>> initialSelection = context.getInitialSelection();
      if (!initialSelection.isEmpty())
      {
         return getProjectFactory().containsProject(initialSelection.get());
      }
      return false;
   }

   /**
    * Returns the selected project. null if no project is found
    */
   protected Project getSelectedProject(UIContext context)
   {
      Project project = null;
      UISelection<FileResource<?>> initialSelection = context.getInitialSelection();
      if (!initialSelection.isEmpty())
      {
         project = getProjectFactory().findProject(initialSelection.get());
      }
      return project;
   }

   /**
    * Returns a reference to the {@link ProjectFactory}.
    */
   protected ProjectFactory getProjectFactory()
   {
      return projectFactory;
   }

}
