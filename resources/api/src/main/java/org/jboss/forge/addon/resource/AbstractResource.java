/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.resource;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jboss.forge.addon.facets.AbstractFaceted;
import org.jboss.forge.addon.resource.util.ResourcePathResolver;
import org.jboss.forge.furnace.util.Streams;

/**
 * @author Mike Brock <cbrock@redhat.com>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public abstract class AbstractResource<T> extends AbstractFaceted<ResourceFacet> implements Resource<T>
{
   private final ResourceFactory resourceFactory;
   private Resource<?> parent;

   protected AbstractResource(final ResourceFactory factory, final Resource<?> parent)
   {
      if (factory == null)
         throw new IllegalArgumentException("ResourceFactory must not be null.");

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

   /**
    * Set the parent {@link Resource}.
    */
   protected void setParent(Resource<?> parent)
   {
      this.parent = parent;
   }

   @Override
   public <R extends Resource<?>> R reify(final Class<R> type)
   {
      if (type.isInstance(this))
      {
         return type.cast(this);
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
      List<Resource<?>> result = new ArrayList<>();
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
   public String getContents()
   {
      InputStream stream = getResourceInputStream();
      try
      {
         return Streams.toString(stream);
      }
      finally
      {
         Streams.closeQuietly(stream);
      }
   }

   @Override
   public String getContents(Charset charset)
   {
      InputStream stream = getResourceInputStream();
      try
      {
         return Streams.toString(stream, charset);
      }
      finally
      {
         Streams.closeQuietly(stream);
      }
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

   @Override
   public <F extends ResourceFacet> boolean supports(F facet)
   {
      return facet != null;
   }

   @Override
   public List<Resource<?>> resolveChildren(String path)
   {
      ResourcePathResolver resolver = new ResourcePathResolver(getResourceFactory(), this, path);
      return resolver.resolve();
   }

}
