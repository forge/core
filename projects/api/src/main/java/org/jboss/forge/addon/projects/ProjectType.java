/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.projects;

import org.jboss.forge.addon.projects.stacks.Stack;
import org.jboss.forge.addon.projects.stacks.StackSupport;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UINavigationContext;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;

/**
 * Provides additional project configuration for use during new project creation.
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public interface ProjectType extends StackSupport
{
   /**
    * Return the human-readable name for this {@link ProjectType}. This should be relatively unique.
    */
   String getType();

   /**
    * Return the {@link UIWizardStep} {@link Class} that begins the {@link Project} configuration of this
    * {@link ProjectType}.
    * 
    * 
    * The default implementation returns <code>null</code>. Implementations are encouraged to override the
    * {@link #next(UINavigationContext)} method instead.
    * 
    */
   default Class<? extends UIWizardStep> getSetupFlow()
   {
      return null;
   }

   /**
    * Return the {@link NavigationResult} that begins {@link Project} configuration of this {@link ProjectType}.
    * 
    * The default implementation calls <code>Results.navigateTo(getSetupFlow())</code>
    * 
    * @param context the current {@link UINavigationContext}
    * 
    * @return a {@link NavigationResult} with the next steps to follow
    */
   default NavigationResult next(UINavigationContext context)
   {
      return Results.navigateTo(getSetupFlow());
   }

   /**
    * Return all {@link ProjectFacet} {@link Class} types required by a {@link Project} of this {@link ProjectType}.
    */
   Iterable<Class<? extends ProjectFacet>> getRequiredFacets();

   /**
    * Defines the priority of this {@link ProjectType}. Lower values receive a higher priority.
    */
   int priority();

   /**
    * Returns if this {@link ProjectType} is enabled in the current {@link UIContext}
    */
   default boolean isEnabled(UIContext context)
   {
      return true;
   }

   @Override
   default boolean supports(Stack stack)
   {
      // By default, check if the required facets are supported by the given stack
      Iterable<Class<? extends ProjectFacet>> requiredFacets = getRequiredFacets();
      if (stack != null && requiredFacets != null)
      {
         for (Class<? extends ProjectFacet> requiredFacet : requiredFacets)
         {
            if (stack.supports(requiredFacet))
            {
               return true;
            }
         }
      }
      return false;
   }
}
