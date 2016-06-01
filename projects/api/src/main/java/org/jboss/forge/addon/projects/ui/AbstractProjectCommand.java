/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.projects.ui;

import java.util.Optional;
import java.util.Set;

import org.jboss.forge.addon.facets.constraints.FacetInspector;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.Projects;
import org.jboss.forge.addon.projects.stacks.Stack;
import org.jboss.forge.addon.projects.stacks.StackInspector;
import org.jboss.forge.addon.projects.stacks.annotations.StackConstraints;
import org.jboss.forge.addon.ui.command.AbstractUICommand;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIContextProvider;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.input.SelectComponent;
import org.jboss.forge.addon.ui.input.UISelectOne;

/**
 * Supports {@link Project} specific operations on {@link UICommand} sub-classes. Also enables the use of
 * {@link StackConstraints} on implementations for which {@link AbstractProjectCommand#isProjectRequired()} returns
 * <code>true</code>.
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public abstract class AbstractProjectCommand extends AbstractUICommand
{
   @Override
   public boolean isEnabled(UIContext context)
   {
      return ((isProjectRequired() && containsProject(context) && constraintsSatisfied(context))
               && super.isEnabled(context)) || (!isProjectRequired() && super.isEnabled(context));
   }

   private boolean constraintsSatisfied(UIContext context)
   {
      Class<?> type = getMetadata(context).getType();
      Project project = getSelectedProject(context);
      Set<Class<ProjectFacet>> facets = FacetInspector.getRequiredFacets(type);
      Set<Class<ProjectFacet>> stackFacets = StackInspector.getAllRelatedFacets(type);
      return FacetInspector.isConstraintSatisfied(project, facets)
               && StackInspector.isConstraintSatisfied(project, stackFacets);
   }

   /**
    * Implementations should return <code>true</code> if a {@link Project} is required in order for this command to
    * execute. If <code>false</code>, this command will run without a {@link Project} selected in the current
    * {@link UIContext}. Returning <code>true</code> also enables the use of {@link StackConstraints} on the
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
      Project selectedProject = Projects.getSelectedProject(getProjectFactory(), context);
      if (isProjectRequired() && selectedProject == null)
      {
         throw new IllegalStateException("A project is required in the current context");
      }
      return selectedProject;
   }

   /**
    * Returns a reference to the {@link ProjectFactory}.
    */
   protected abstract ProjectFactory getProjectFactory();

   /**
    * Filters the given value choices according the current enabled stack
    * 
    * @param select the {@link SelectComponent} containing the value choices to be filtered
    * @return <code>true</code> if it should be displayed in the UI
    */
   protected <T extends ProjectFacet> boolean filterValueChoicesFromStack(Project project, UISelectOne<T> select)
   {
      boolean result = true;
      Optional<Stack> stackOptional = project.getStack();
      // Filtering only supported facets
      if (stackOptional.isPresent())
      {
         Stack stack = stackOptional.get();
         Iterable<T> valueChoices = select.getValueChoices();
         Set<T> filter = stack.filter(select.getValueType(), valueChoices);
         select.setValueChoices(filter);
         if (filter.size() == 1)
         {
            select.setDefaultValue(filter.iterator().next());
            result = false;
         }
         else if (filter.size() == 0)
         {
            result = false;
         }
         // FIXME: JBIDE-21584: Contains return false because of proxy class
         // else if (!filter.contains(select.getValue()))
         // {
         // select.setDefaultValue((T) null);
         // }
      }
      return result;
   }

}
