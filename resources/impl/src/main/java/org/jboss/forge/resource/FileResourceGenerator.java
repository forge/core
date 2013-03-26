package org.jboss.forge.resource;

import java.io.File;

import org.jboss.forge.container.services.Exported;

@Exported
public class FileResourceGenerator implements ResourceGenerator<FileResource<?>, File>
{
   @Override
   public boolean handles(Class<?> type, Object resource)
   {
      if (resource instanceof File)
      {
         return true;
      }
      return false;
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T extends Resource<File>> T getResource(ResourceFactory factory, Class<FileResource<?>> type, File resource)
   {
      if (DirectoryResource.class.isAssignableFrom(type) || (resource.exists() && resource.isDirectory()))
         return (T) new DirectoryResourceImpl(factory, resource);
      return (T) new FileResourceImpl(factory, resource);
   }

   @Override
   public <T extends Resource<File>> Class<?> getResourceType(Class<FileResource<?>> type, File resource)
   {
      if (DirectoryResource.class.isAssignableFrom(type) || (resource.exists() && resource.isDirectory()))
         return DirectoryResource.class;
      return FileResourceImpl.class;
   }
}
