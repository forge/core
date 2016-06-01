/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.projects.facets.exceptions;

public class PluginNotFoundException extends RuntimeException
{
   private static final long serialVersionUID = -2879527163396471926L;

   public PluginNotFoundException()
   {
      super("No message");
   }

   public PluginNotFoundException(final String groupId, final String artifactId)
   {
      super("Plugin " + groupId + ":" + artifactId + " was not found");
   }

   public PluginNotFoundException(String message, Throwable cause)
   {
      super(message, cause);
   }

   public PluginNotFoundException(String message)
   {
      super(message);
   }
}
