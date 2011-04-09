package org.jboss.seam.forge.maven.facets.exceptions;

public class PluginNotFoundException extends RuntimeException
{
   public PluginNotFoundException(String groupId, String artifactId) {
      super("Plugin " + groupId + ":" + artifactId + " was not found");
   }
}
