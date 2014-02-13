/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.ui;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.Projects;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.command.UICommandEnricher;
import org.jboss.forge.addon.ui.context.UIContext;

/**
 * Adds the Setup steps on {@link AbstractJavaEECommand}
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@Singleton
public class JavaEEUICommandEnricher implements UICommandEnricher
{
   @Inject
   private ProjectFactory projectFactory;

   @SuppressWarnings("unchecked")
   @Override
   public UICommand enrich(UIContext context, UICommand original)
   {
      final UICommand result;
      if (original instanceof AbstractJavaEECommand && Projects.containsProject(projectFactory, context))
      {
         List<Class<? extends UICommand>> previousSteps = ((AbstractJavaEECommand) original).getSetupSteps(context);
         if (previousSteps.isEmpty())
         {
            result = original;
         }
         else
         {
            Class<? extends UICommand> classes[] = new Class[previousSteps.size() + 1];
            previousSteps.toArray(classes);
            classes[classes.length - 1] = (Class<? extends UICommand>) original.getMetadata(context).getType();
            result = new CompositeWizard(original, classes);
         }
      }
      else
      {
         result = original;
      }
      return result;
   }
}
