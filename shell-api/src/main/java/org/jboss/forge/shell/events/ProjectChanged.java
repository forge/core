/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.shell.events;

import org.jboss.forge.project.Project;

/**
 * An event that notifies observers immediately after the current {@link Project} has changed.
 * <p>
 * <strong>For example:</strong>
 * <p>
 * <code>public void myObserver(@Observes {@link ProjectChanged} event)<br/>
 * {<br/>
 *    // do something<br/>
 * }<br/>
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public final class ProjectChanged
{
   private final Project oldProject;
   private final Project newProject;

   public ProjectChanged(final Project oldProject, final Project newProject)
   {
      this.oldProject = oldProject;
      this.newProject = newProject;
   }

   /**
    * @return the old {@link Project}
    */
   public Project getOldProject()
   {
      return oldProject;
   }

   /**
    * @return the new {@link Project}
    */
   public Project getNewProject()
   {
      return newProject;
   }
}
