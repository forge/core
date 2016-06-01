/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.projects.impl;

import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectListener;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class MockProjectListener implements ProjectListener
{
   static volatile Project project;

   @Override
   public void projectCreated(Project project)
   {
      MockProjectListener.project = project;
   }
}
