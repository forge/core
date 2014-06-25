package org.jboss.forge.addon.resource;

import java.io.File;

public class FileResourceGenerator implements ResourceGenerator<FileResource<?>, File>
{
   @Override
   public boolean handles(Class<?> type, Object resource)
   {
      return (resource instanceof File);
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T extends Resource<File>> T getResource(ResourceFactory factory, Class<FileResource<?>> type, File resource)
   {
      ResourceOperations<File> fileOperations = factory.getResourceOperations(File.class);
      if ((DirectoryResource.class.isAssignableFrom(type) && (!fileOperations.exists(resource)))
               || (fileOperations.existsAndIsDirectory(resource)))
         return (T) new DirectoryResourceImpl(factory, resource);
      return (T) new FileResourceImpl(factory, resource);
   }

   @Override
   public <T extends Resource<File>> Class<?> getResourceType(ResourceFactory factory, Class<FileResource<?>> type,
            File resource)
   {
      ResourceOperations<File> fileOperations = factory.getResourceOperations(File.class);
      if ((DirectoryResource.class.isAssignableFrom(type) && (!fileOperations.exists(resource)))
               || (fileOperations.existsAndIsDirectory(resource)))
         return DirectoryResource.class;
      return FileResource.class;
   }
}
