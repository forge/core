/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.projects.building;

import java.util.LinkedHashSet;
import java.util.Set;

import org.jboss.forge.addon.projects.building.BuildMessage.Severity;

/**
 * Creates a {@link BuildResult} object
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class BuildResultBuilder implements BuildResult
{
   private boolean success = true;
   private Set<BuildMessage> messages = new LinkedHashSet<>();

   private BuildResultBuilder()
   {
   }

   public static BuildResultBuilder create()
   {
      return new BuildResultBuilder();
   }

   public BuildResultBuilder succeeded()
   {
      this.success = true;
      return this;
   }

   public BuildResultBuilder failed()
   {
      this.success = false;
      return this;
   }

   public BuildResultBuilder status(boolean status)
   {
      this.success = status;
      return this;
   }

   public BuildResultBuilder addMessage(Severity severity, String message)
   {
      this.messages.add(new BuildMessageImpl(severity, message));
      return this;
   }

   public BuildResultBuilder addMessage(BuildMessage message)
   {
      this.messages.add(message);
      return this;
   }

   public BuildResult build()
   {
      return this;
   }

   @Override
   public boolean isSuccess()
   {
      return success;
   }

   @Override
   public Iterable<BuildMessage> getMessages()
   {
      return messages;
   }
}
