/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.resource.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFilter;
import org.jboss.forge.furnace.util.Assert;

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
    * Returns the {@link Byte} array message digest of {@link #getResourceInputStream()} using the default MD5
    * {@link MessageDigest}.
    */
   public static byte[] getDigest(Resource<?> resource)
   {
      try
      {
         return getDigest(resource, MessageDigest.getInstance("MD5"));
      }
      catch (NoSuchAlgorithmException e)
      {
         throw new IllegalStateException("Error calculating digest for resource [" + resource.getFullyQualifiedName()
                  + "]", e);
      }
   }

   /**
    * Returns the {@link Byte} array message digest of {@link #getResourceInputStream()} using the given
    * {@link MessageDigest}.
    */
   public static byte[] getDigest(Resource<?> resource, MessageDigest digest)
   {
      try (InputStream stream = resource.getResourceInputStream();
               DigestInputStream digestStream = new DigestInputStream(stream, digest))
      {
         byte[] buffer = new byte[16384];
         while (digestStream.read(buffer, 0, buffer.length) != -1)
         {
         }
      }
      catch (IOException e)
      {
         throw new IllegalStateException("Error calculating digest for resource [" + resource.getFullyQualifiedName()
                  + "]", e);
      }
      return digest.digest();
   }

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
      List<E> result = new ArrayList<>();
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

   public static boolean isParentOf(Resource<?> parent, Resource<?> child)
   {
      Assert.notNull(parent, "Parent resource must not be null.");
      Assert.notNull(child, "Child resource must not be null.");

      while (child.getParent() != null)
      {
         if (parent == child.getParent() || parent.equals(child.getParent()))
            return true;

         child = child.getParent();
      }
      return false;
   }
}
