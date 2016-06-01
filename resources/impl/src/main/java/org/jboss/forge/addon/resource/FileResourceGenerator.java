/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
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
      FileOperations fileOperations = factory.getFileOperations();
      if ((DirectoryResource.class.isAssignableFrom(type) && (!fileOperations.fileExists(resource) || fileOperations
               .fileExistsAndIsDirectory(resource))) || (fileOperations.fileExistsAndIsDirectory(resource)))
         return (T) new DirectoryResourceImpl(factory, resource);
      return (T) new FileResourceImpl(factory, resource);
   }

   @Override
   public <T extends Resource<File>> Class<?> getResourceType(ResourceFactory factory, Class<FileResource<?>> type,
            File resource)
   {
      FileOperations fileOperations = factory.getFileOperations();
      if ((DirectoryResource.class.isAssignableFrom(type) && (!fileOperations.fileExists(resource) || fileOperations
               .fileExistsAndIsDirectory(resource))) || (fileOperations.fileExistsAndIsDirectory(resource)))
         return DirectoryResource.class;
      return FileResource.class;
   }
}
