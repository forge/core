/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.resources;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.jboss.forge.project.services.ResourceFactory;

/**
 * @author Mike Brock <cbrock@redhat.com>
 */
public abstract class AbstractResource<T> implements Resource<T>
{
   protected final ResourceFactory resourceFactory;
   protected Resource<?> parent;

   protected EnumSet<ResourceFlag> flags;

   protected AbstractResource(final ResourceFactory factory, final Resource<?> parent)
   {
      this.resourceFactory = factory;
      this.parent = parent;
   }

   @Override
   public ResourceFactory getResourceFactory()
   {
      return resourceFactory;
   }

   @Override
   public String getFullyQualifiedName()
   {
      return getParent() != null ? getParent().getFullyQualifiedName() + "/" + getName() : getName();
   }

   @Override
   public Resource<?> getParent()
   {
      return parent;
   }

   @Override
   public void setFlag(final ResourceFlag flag)
   {
      if (flags == null)
      {
         flags = EnumSet.of(flag);
      }
      else
      {
         flags.add(flag);
      }
   }

   @Override
   public void unsetFlag(final ResourceFlag flag)
   {
      if (flags != null)
      {
         flags.remove(flag);
      }
   }

   @Override
   public boolean isFlagSet(final ResourceFlag flag)
   {
      return (flags != null) && flags.contains(flag);
   }

   @Override
   public Set<ResourceFlag> getFlags()
   {
      return Collections.unmodifiableSet(flags);
   }

   @Override
   @SuppressWarnings("unchecked")
   public <R extends Resource<?>> R reify(final Class<R> type)
   {
      if (type.isAssignableFrom(this.getClass()))
      {
         return (R) this;
      }
      else
      {
         return null;
      }
   }

   /**
    * Strategy method for returning child resources. Subclasses should implement or override this method.
    * 
    * @return
    */
   protected abstract List<Resource<?>> doListResources();

   @Override
   public synchronized List<Resource<?>> listResources()
   {
      List<Resource<?>> resources = doListResources();

      Collections.sort(resources, new FQNResourceComparator());
      return resources;
   }

   @Override
   public synchronized List<Resource<?>> listResources(final ResourceFilter filter)
   {
      List<Resource<?>> result = new ArrayList<Resource<?>>();
      for (Resource<?> resource : doListResources())
      {
         if (filter.accept(resource))
         {
            result.add(resource);
         }
      }

      Collections.sort(result, new FQNResourceComparator());

      return result;
   }

   @Override
   public boolean equals(final Object obj)
   {
      if (obj == this)
      {
         return true;
      }

      if (obj instanceof Resource<?>)
      {
         return ((Resource<?>) obj).getFullyQualifiedName().equals(getFullyQualifiedName());
      }
      return false;
   }

   @Override
   public int hashCode()
   {
      return getFullyQualifiedName().hashCode();
   }

   private static class FQNResourceComparator implements Comparator<Resource<?>>
   {
      @Override
      public int compare(Resource<?> left, Resource<?> right)
      {
         return left.getFullyQualifiedName().compareTo(right.getFullyQualifiedName());
      }
   }
}
