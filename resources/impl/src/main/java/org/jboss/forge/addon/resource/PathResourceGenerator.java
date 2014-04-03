package org.jboss.forge.addon.resource;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Resource generator for {@link PathResource} instances
 *
 * @author Shane Bryzak
 *
 */
public class PathResourceGenerator implements ResourceGenerator<PathResource<?>, Path>
{
   @Override
   public boolean handles(Class<?> type, Object resource)
   {
      return (resource instanceof Path);
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T extends Resource<Path>> T getResource(ResourceFactory factory, Class<PathResource<?>> type, Path resource)
   {
      try
      {
         return (T) new PathResourceImpl(factory, resource);
      }
      catch (IOException ex)
      {
         throw new ResourceException(ex);
      }
   }

   @Override
   public <T extends Resource<Path>> Class<?> getResourceType(ResourceFactory factory, Class<PathResource<?>> type,
            Path resource)
   {
      return PathResource.class;
   }
}