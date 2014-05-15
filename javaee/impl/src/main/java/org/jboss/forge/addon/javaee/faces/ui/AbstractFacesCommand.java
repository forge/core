/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.faces.ui;

import org.jboss.forge.addon.javaee.faces.FacesFacet;
import org.jboss.forge.addon.javaee.ui.AbstractJavaEECommand;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.ui.command.PrerequisiteCommandsProvider;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.navigation.NavigationResultBuilder;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public abstract class AbstractFacesCommand extends AbstractJavaEECommand implements PrerequisiteCommandsProvider
{

   public AbstractFacesCommand()
   {
      super();
   }

   @Override
   public NavigationResult getPrerequisiteCommands(UIContext context)
   {
      NavigationResultBuilder builder = NavigationResultBuilder.create();
      Project project = getSelectedProject(context);
      if (project != null)
      {
         if (!project.hasFacet(FacesFacet.class))
         {
            builder.add(FacesSetupWizardImpl.class);
         }
      }
      return builder.build();
   }

}