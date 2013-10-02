/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

import org.jboss.forge.furnace.util.OperatingSystemUtils;

/**
 * A standard, build-in, resource for representing directories on the file-system.
 * 
 * @author Mike Brock
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class DirectoryResourceImpl extends AbstractFileResource<DirectoryResource> implements DirectoryResource
{
   private volatile List<Resource<?>> listCache;

   public DirectoryResourceImpl(final ResourceFactory factory, final File file)
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

         File[] files = getFileOperations().listFiles(file);
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
    * Obtain a reference to the child {@link DirectoryResourceImpl}. If that resource does not exist, return a new
    * instance. If the resource exists and is not a {@link DirectoryResourceImpl}, throw {@link ResourceException}
    */
   @Override
   public DirectoryResourceImpl getChildDirectory(final String name) throws ResourceException
   {
      Resource<?> result = getChild(name);
      if (!(result instanceof DirectoryResourceImpl))
      {
         if (result.exists())
         {
            throw new ResourceException("The resource [" + result.getFullyQualifiedName()
                     + "] is not a DirectoryResource");
         }
      }

      if (!(result instanceof DirectoryResourceImpl))
      {
         result = new DirectoryResourceImpl(resourceFactory, new File(file.getAbsoluteFile(), name));
      }
      return (DirectoryResourceImpl) result;
   }

   @Override
   public DirectoryResourceImpl getOrCreateChildDirectory(String name)
   {
      DirectoryResourceImpl child = getChildDirectory(name);
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
   @Override
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
   public DirectoryResourceImpl createTempResource()
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
   public DirectoryResourceImpl createFrom(final File file)
   {
      if (getFileOperations().fileExists(file) && !getFileOperations().fileExistsAndIsDirectory(file))
      {
         throw new ResourceException("File reference is not a directory: " + file.getAbsolutePath());
      }
      else if (!getFileOperations().fileExists(file))
      {
         getFileOperations().mkdirs(file);
      }

      return new DirectoryResourceImpl(resourceFactory, file);
   }

   @Override
   public synchronized DirectoryResource getParent()
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
      return (DirectoryResource) parent;
   }

   @Override
   public String getName()
   {
      String fileName = file.getName();
      // Windows: drive letter is needed. If filename is empty, we are on a root folder
      return (OperatingSystemUtils.isWindows() && fileName.length() == 0) ? file.getPath() : fileName;
   }

   @Override
   public boolean equals(final Object obj)
   {
      return (obj instanceof DirectoryResourceImpl) && ((DirectoryResourceImpl) obj).file.equals(file);
   }

   @Override
   public long getSize()
   {
      throw new UnsupportedOperationException("getSize not supported for DirectoryResource objects");
   }

   @Override
   public DirectoryResource setContents(char[] data)
   {
      throw new UnsupportedOperationException("setContents(char[]) is not supported on DirectoryResource objects");
   }

   @Override
   public DirectoryResource setContents(InputStream data)
   {
      throw new UnsupportedOperationException("setContents(InputStream) is not supported on DirectoryResource objects");
   }

   @Override
   public DirectoryResource setContents(String data)
   {
      throw new UnsupportedOperationException("setContents(String) is not supported on DirectoryResource objects");
   }

   @Override
   public String getContents()
   {
      throw new UnsupportedOperationException("getContents() is not supported on DirectoryResource objects");
   }

   @Override
   public DirectoryResource setContents(char[] data, Charset charset)
   {
      throw new UnsupportedOperationException(
               "setContents(char[], Charset) is not supported on DirectoryResource objects");
   }

   @Override
   public DirectoryResource setContents(String data, Charset charset)
   {
      throw new UnsupportedOperationException(
               "setContents(String, Charset) is not supported on DirectoryResource objects");
   }

   @Override
   public String getContents(Charset charset)
   {
      throw new UnsupportedOperationException(
               "getContents(Charset) is not supported on DirectoryResource objects");
   }
}