/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.project.dependencies.events;

import java.util.ArrayList;
import java.util.List;

import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.Dependency;

/**
 * Fired when a dependency is being updated.
 *
 * It is possible to veto the action by calling the {@link UpdatingDependency#veto(String)} method
 *
 */
public final class UpdatingDependency
{
   private Dependency from;
   private Dependency to;

   private Project project;

   private boolean vetoed = false;
   private List<String> messages;

   public UpdatingDependency(Project project, Dependency from, Dependency to)
   {
      this.project = project;
      this.from = from;
      this.to = to;
      this.messages = new ArrayList<String>();
   }

   public Dependency getFrom()
   {
      return from;
   }

   public Dependency getTo()
   {
      return to;
   }

   public Project getProject()
   {
      return project;
   }

   public boolean isVetoed()
   {
      return vetoed;
   }

   /**
    * Abort the Update.
    */
   public void veto(String message)
   {
      vetoed = true;
      this.messages.add(message);
   }

   /**
    * @return A list of messages from the vetoers
    */
   public List<String> getMessages()
   {
      return messages;
   }
}
