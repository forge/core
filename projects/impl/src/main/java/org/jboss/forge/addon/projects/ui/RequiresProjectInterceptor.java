/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.projects.ui;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.annotations.RequiresProject;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UISelection;

/**
 * This decorator is invoked for {@link UICommand} instances that requires a project
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@Priority(Integer.MAX_VALUE)
@Interceptor
@RequiresProject
public class RequiresProjectInterceptor
{
   @Inject
   ProjectFactory projectFactory;

   @AroundInvoke
   public Object assertRequiresProjectIsEnabled(InvocationContext context) throws Exception
   {
      final Object result;
      if (context.getTarget() instanceof UICommand && context.getMethod().getName().equals("isEnabled"))
      {
         UIContext uiContext = (UIContext) context.getParameters()[0];
         if (containsProject(uiContext))
         {
            result = context.proceed();
         }
         else
         {
            result = Boolean.FALSE;
         }
      }
      else
      {
         result = context.proceed();
      }
      return result;
   }

   /**
    * Returns <code>true</code> if a {@link Project} exists in the current {@link UISelection}.
    */
   private boolean containsProject(UIContext context)
   {
      UISelection<FileResource<?>> initialSelection = context.getInitialSelection();
      if (!initialSelection.isEmpty())
      {
         return projectFactory.containsProject(initialSelection.get());
      }
      return false;
   }

}
