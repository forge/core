/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jboss.modules.ConcurrentClassLoader;

public class CompositeClassLoader extends ConcurrentClassLoader
{
   private final List<ConcurrentClassLoader> classLoaders = Collections
            .synchronizedList(new ArrayList<ConcurrentClassLoader>());

   public CompositeClassLoader()
   {
   }

   /**
    * Add a loader to the internal List of loaders. Loaders will be used in the reverse order from which they were
    * added.
    */
   public void add(ConcurrentClassLoader loader)
   {
      if (loader != null && !classLoaders.contains(loader) && !this.equals(loader))
      {
         classLoaders.add(0, loader);
      }
   }

   /**
    * Get an unmodifiable view of this Loader's internal list.
    */
   protected List<ConcurrentClassLoader> getClassLoaders()
   {
      return Collections.unmodifiableList(classLoaders);
   }

   @Override
   protected Class<?> findClass(String className, boolean exportsOnly, boolean resolve) throws ClassNotFoundException
   {
      for (ConcurrentClassLoader loader : classLoaders)
      {
         try
         {
            Class<?> found = loader.loadClass(className, resolve);
            return found;
         }
         catch (ClassNotFoundException e)
         {
            // I don't care yet...
         }
      }
      throw new ClassNotFoundException(className);
   }

   @Override
   protected URL findResource(String name, boolean exportsOnly)
   {
      for (ConcurrentClassLoader loader : classLoaders)
      {
         URL resource = loader.getResource(name);
         if (resource != null)
            return resource;
      }
      return null;
   }

   @Override
   protected Enumeration<URL> findResources(String name, boolean exportsOnly) throws IOException
   {
      Set<URL> urls = new HashSet<URL>();
      for (ConcurrentClassLoader loader : classLoaders)
      {
         Enumeration<URL> resources = loader.getResources(name);
         while (resources.hasMoreElements())
         {
            urls.add(resources.nextElement());
         }
      }

      return Collections.enumeration(urls);
   }

   @Override
   protected InputStream findResourceAsStream(String name, boolean exportsOnly)
   {
      for (ConcurrentClassLoader loader : classLoaders)
      {
         InputStream stream = loader.getResourceAsStream(name);
         if (stream != null)
         {
            return stream;
         }
      }
      return null;
   }

   @Override
   public String toString()
   {
      return "CompositeClassLoader [classLoaders=" + classLoaders + "]";
   }
}
