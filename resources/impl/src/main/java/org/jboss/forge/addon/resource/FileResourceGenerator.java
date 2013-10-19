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
      FileResourceOperations fileOperations = factory.getFileOperations();
      if ((DirectoryResource.class.isAssignableFrom(type) && (!fileOperations.fileExists(resource) || fileOperations
               .fileExistsAndIsDirectory(resource))) || (fileOperations.fileExistsAndIsDirectory(resource)))
         return (T) new DirectoryResourceImpl(factory, resource);
      return (T) new FileResourceImpl(factory, resource);
   }

   @Override
   public <T extends Resource<File>> Class<?> getResourceType(Class<FileResource<?>> type, File resource)
   {
      return type;
   }
}
