package org.jboss.forge.addon.git.gitignore.resources;

import static org.jboss.forge.addon.git.constants.GitConstants.GITIGNORE;

import java.io.File;

import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.resource.ResourceGenerator;

public class GitIgnoreResourceGenerator implements ResourceGenerator<GitIgnoreResource, File>
{

   @Override
   public boolean handles(Class<?> type, Object resource)
   {
      if (resource instanceof File && ((File) resource).getName().equals(GITIGNORE))
      {
         return true;
      }
      return false;
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T extends Resource<File>> T getResource(ResourceFactory factory, Class<GitIgnoreResource> type, File resource)
   {
      return (T) new GitIgnoreResource(factory, resource);
   }

   @Override
   public <T extends Resource<File>> Class<?> getResourceType(ResourceFactory factory, Class<GitIgnoreResource> type,
            File resource)
   {
      return GitIgnoreResource.class;
   }

}
