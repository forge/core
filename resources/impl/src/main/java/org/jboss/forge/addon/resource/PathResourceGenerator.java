package org.jboss.forge.addon.resource;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Resource generator for {@link PathResource} instances
 *
 * @author Shane Bryzak
 *
 */
public class PathResourceGenerator implements ResourceGenerator<PathResource, Object>
{
   @Override
   public boolean handles(Class<?> type, Object resource)
   {
      if (resource instanceof Path)
      {
         return true;
      }
      else if (resource instanceof CharSequence)
      {
         try
         {
            Paths.get(resource.toString());
            return true;
         }
         catch (InvalidPathException ipe)
         {
            return false;
         }
      }
      return false;
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T extends Resource<Object>> T getResource(ResourceFactory factory, Class<PathResource> type,
            Object resource)
   {
      try
      {
         if (resource instanceof Path)
         {
            Resource<?> res = new PathResourceImpl(factory, (Path) resource);
            return (T) res;
         }
         else
         {
            Resource<?> res = new PathResourceImpl(factory, Paths.get(resource.toString()));
            return (T) res;
         }
      }
      catch (IOException ex)
      {
         throw new ResourceException(ex);
      }
   }

   @Override
   public <T extends Resource<Object>> Class<?> getResourceType(ResourceFactory factory, Class<PathResource> type,
            Object resource)
   {
      return PathResource.class;
   }
}