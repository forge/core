/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.projects.ui;

import javax.inject.Inject;

import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.Projects;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.furnace.util.Predicate;

/**
 * Requires a project to be enabled
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class RequiresProjectPredicate implements Predicate<UIContext>
{
   @Inject
   private ProjectFactory projectFactory;

   @Override
   public boolean accept(UIContext context)
   {
      return Projects.containsProject(projectFactory, context);
   }
}
