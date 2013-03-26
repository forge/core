package org.jboss.forge.resource;

import java.io.InputStream;

/**
 * An abstract implementation of a virtual resource handle.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class VirtualResource<T> extends AbstractResource<T>
{
   protected VirtualResource(ResourceFactory factory, final Resource<?> parent)
   {
      super(factory, parent);
   }

   @Override
   public Resource<?> getChild(final String name)
   {
      throw new RuntimeException("not implemented");
   }

   @Override
   public Resource<T> createFrom(T resource)
   {
      throw new RuntimeException("not implemented");
   }

   @Override
   public InputStream getResourceInputStream()
   {
      throw new ResourceException("not supported");
   }

   @Override
   public boolean exists()
   {
      return true;
   }
}