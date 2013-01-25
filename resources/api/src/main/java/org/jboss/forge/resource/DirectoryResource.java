/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.resource;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.jboss.forge.facet.Facet;

/**
 * A standard, build-in, resource for representing directories on the file-system.
 * 
 * @author Mike Brock
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class DirectoryResource extends FileResource<DirectoryResource>
{
   private volatile List<Resource<?>> listCache;

   public DirectoryResource(final ResourceFactory factory, final File file)
   {
      super(factory, file);
   }

   @Override
   protected List<Resource<?>> doListResources()
   {
      if (isStale())
      {
         listCache = null;
      }

      if (listCache == null)
      {
         listCache = new LinkedList<Resource<?>>();

         File[] files = file.listFiles();
         if (files != null)
         {
            for (File f : files)
            {
               listCache.add(resourceFactory.create(f));
            }
         }
      }

      return listCache;
   }

   /**
    * Obtain a reference to the child resource.
    */
   @Override
   public Resource<?> getChild(final String name)
   {
      return resourceFactory.create(new File(file.getAbsolutePath(), name));
   }

   /**
    * Obtain a reference to the child {@link DirectoryResource}. If that resource does not exist, return a new instance.
    * If the resource exists and is not a {@link DirectoryResource}, throw {@link ResourceException}
    */
   public DirectoryResource getChildDirectory(final String name) throws ResourceException
   {
      Resource<?> result = getChild(name);
      if (!(result instanceof DirectoryResource))
      {
         if (result.exists())
         {
            throw new ResourceException("The resource [" + result.getFullyQualifiedName()
                     + "] is not a DirectoryResource");
         }
      }

      if (!(result instanceof DirectoryResource))
      {
         result = new DirectoryResource(resourceFactory, new File(file.getAbsoluteFile(), name));
      }
      return (DirectoryResource) result;
   }

   public DirectoryResource getOrCreateChildDirectory(String name)
   {
      DirectoryResource child = getChildDirectory(name);
      if (!child.exists())
      {
         child.mkdir();
      }
      return child;
   }

   /**
    * Using the given type, obtain a reference to the child resource of the given type. If the result is not of the
    * requested type and does not exist, return null. If the result is not of the requested type and exists, throw
    * {@link ResourceException}
    */
   @SuppressWarnings("unchecked")
   public <E, T extends Resource<E>> T getChildOfType(final Class<T> type, final String name) throws ResourceException
   {
      T result;
      Resource<?> child = getChild(name);
      if (type.isAssignableFrom(child.getClass()))
      {
         result = (T) child;
      }
      else if (child.exists())
      {
         throw new ResourceException("Requested resource [" + name + "] was not of type [" + type.getName()
                  + "], but was instead [" + child.getClass().getName() + "]");
      }
      else
      {
         E underlyingResource = (E) child.getUnderlyingResourceObject();
         result = resourceFactory.create(type, underlyingResource);
      }
      return result;
   }

   @Override
   public DirectoryResource createTempResource()
   {
      try
      {
         File tempFile = File.createTempFile("forgetemp", "");
         tempFile.delete();
         return createFrom(tempFile);
      }
      catch (IOException e)
      {
         throw new ResourceException(e);
      }
   }

   @Override
   public DirectoryResource createFrom(final File file)
   {
      if (file.exists() && !file.isDirectory())
      {
         throw new ResourceException("File reference is not a directory: " + file.getAbsolutePath());
      }
      else if (!file.exists())
      {
         file.mkdirs();
      }

      return new DirectoryResource(resourceFactory, file);
   }

   @Override
   public synchronized Resource<?> getParent()
   {
      if (parent == null)
      {
         File parentFile = file.getParentFile();
         if (parentFile == null)
         {
            return null;
         }

         parent = createFrom(parentFile);
      }
      return parent;
   }

   @Override
   public String getName()
   {
      String fileName = file.getName();
      // Windows: drive letter is needed. If filename is empty, we are on a root folder
      return (OSUtils.isWindows() && fileName.length() == 0) ? file.getPath() : fileName;
   }

   @Override
   public String toString()
   {
      return getName();
   }

   @Override
   public boolean equals(final Object obj)
   {
      return (obj instanceof DirectoryResource) && ((DirectoryResource) obj).file.equals(file);
   }

   @Override
   public boolean supports(Class<? extends Facet<?>> type)
   {
      return false;
   }
}
