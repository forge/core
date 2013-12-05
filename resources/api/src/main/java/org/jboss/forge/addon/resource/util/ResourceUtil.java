/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.resource.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFilter;

/**
 * A set of utilities to work with the resources API.
 *
 * @author Mike Brock
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author Vineet Reynolds
 */
public class ResourceUtil
{
   /**
    * A simple utility method to locate the outermost contextual File reference for the specified resource.
    *
    * @param r resource instance.
    * @return outermost relevant file context.
    */
   public static File getContextFile(Resource<?> r)
   {
      do
      {
         Object o = r.getUnderlyingResourceObject();
         if (o instanceof File)
         {
            return (File) r.getUnderlyingResourceObject();
         }

      }
      while ((r = r.getParent()) != null);

      return null;
   }

   public static DirectoryResource getContextDirectory(final Resource<?> r)
   {
      Resource<?> temp = r;
      do
      {
         if (temp instanceof DirectoryResource)
         {
            return (DirectoryResource) temp;
         }
      }
      while ((temp != null) && ((temp = temp.getParent()) != null));

      return null;
   }

   public static boolean isChildOf(final Resource<?> parent, final Resource<?> isChild)
   {
      Resource<?> r = isChild;
      while ((r = r.getParent()) != null)
      {
         if (r.equals(parent))
         {
            return true;
         }
      }
      return false;
   }

   @SuppressWarnings("unchecked")
   public static <E extends Resource<?>, R extends Collection<E>> R filter(ResourceFilter filter, Collection<E> list)
   {
      List<E> result = new ArrayList<E>();
      for (E resource : list)
      {
         if (filter.accept(resource))
         {
            result.add(resource);
         }
      }
      return (R) result;
   }

   @SuppressWarnings("unchecked")
   public static <E extends Resource<?>, R extends Collection<E>, I extends Collection<Resource<?>>> R filterByType(
            final Class<E> type, final I list)
   {
      ResourceFilter filter = new ResourceFilter()
      {
         @Override
         public boolean accept(Resource<?> resource)
         {
            return type.isAssignableFrom(resource.getClass());
         }
      };

      return (R) filter(filter, list);
   }

}
