/*
 * JBoss, by Red Hat.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
   {}

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
            if ("org.jboss.weld.bootstrap.WeldBootstrap".equals(className)
                     || "org.jboss.weld.bootstrap.api.Bootstrap".equals(className))
            {
               System.out.println(className + " came from " + loader);
            }
            return found;
         }
         catch (ClassNotFoundException e)
         {
            // System.err.println(loader + " did not have class " + className);
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
