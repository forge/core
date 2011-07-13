package org.jboss.forge.maven.facets.exceptions;

public class PluginNotFoundException extends RuntimeException
{
   private static final long serialVersionUID = -2879527163396471926L;

   public PluginNotFoundException(final String groupId, final String artifactId)
   {
      super("Plugin " + groupId + ":" + artifactId + " was not found");
   }
}
