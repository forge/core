package org.jboss.forge.resource;

import java.io.File;

import org.jboss.forge.container.services.Remote;

@Remote
public class FileResourceGenerator implements ResourceGenerator<File>
{
   @Override
   public boolean handles(Object resource)
   {
      if (resource instanceof File)
      {
         return true;
      }
      return false;
   }

   @Override
   public Class<? extends Resource<File>> getResourceType(File resource)
   {
      if (resource.isDirectory())
         return DirectoryResource.class;
      return UnknownFileResource.class;
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T extends Resource<File>> T getResource(ResourceFactory factory, File resource)
   {
      if (resource.isDirectory())
         return (T) new DirectoryResource(factory, resource);
      return (T) new UnknownFileResource(factory, resource);
   }
}
