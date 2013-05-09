/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.shell.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.project.services.ResourceFactory;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.resources.ResourceFilter;
import org.jboss.forge.resources.java.JavaMemberResource;
import org.jboss.forge.resources.java.JavaResource;

/**
 * A set of utilities to work with the resources API.
 *
 * @author Mike Brock
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
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

   public static List<Resource<?>> parsePathspec(final ResourceFactory factory, final Resource<?> resource,
            final String pathspec)
   {
      return new PathspecParser(factory, resource, pathspec).resolve();
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

   @SuppressWarnings("unchecked")
   public static boolean hasAnnotation(Resource<?> resource,
            Class<? extends java.lang.annotation.Annotation> annotationClass) throws FileNotFoundException
   {
      if (resource == null)
      {
         throw new IllegalArgumentException("The parameter 'resource' cannot be null");
      }
      if (annotationClass == null)
      {
         throw new IllegalArgumentException("The parameter 'annotationClass' cannot be null");
      }

      if (resource instanceof JavaResource)
      {
         final JavaClass javaClass = ResourceUtil.getJavaClassFromResource(resource);
         return javaClass.hasAnnotation(annotationClass);
      }
      else if (resource instanceof JavaMemberResource)
      {
         final JavaMemberResource<?> javaMemberResource = (JavaMemberResource<?>) resource;
         return javaMemberResource.getUnderlyingResourceObject().hasAnnotation(annotationClass);
      }
      throw new IllegalArgumentException("The given resource '" + resource.getName() + "' is not a Java resource");
   }

   public static JavaClass getJavaClassFromResource(Resource<?> resource) throws FileNotFoundException
   {
      if (resource == null)
      {
         throw new IllegalArgumentException("The parameter 'resource' cannot be null");
      }
      if (!(resource instanceof JavaResource))
      {
         throw new IllegalArgumentException("The given resource '" + resource.getName() + "' is not a Java resource");
      }

      final JavaResource javaResource = (JavaResource) resource;
      final JavaSource<?> javaSource = javaResource.getJavaSource();
      if (!(javaSource.isClass() || javaSource.isInterface()))
      {
         throw new IllegalArgumentException("The given resource '" + resource.getName()
                  + "' is not a class or an interface");
      }
      return (JavaClass) javaResource.getJavaSource();
   }

}
