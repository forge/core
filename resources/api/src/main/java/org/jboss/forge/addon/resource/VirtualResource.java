package org.jboss.forge.addon.resource;

import java.io.InputStream;

/**
 * An abstract implementation of a virtual resource handle.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class VirtualResource<T> extends AbstractResource<T>
{
   private Resource<?> underlyingResource;

   protected VirtualResource(ResourceFactory factory, final Resource<?> underlyingResource)
   {
      super(factory, null);
      this.underlyingResource = underlyingResource;
   }
   
   public Resource<?> getUnderlyingResource()
   {
      return underlyingResource;
   }

   @Override
   public Resource<?> getParent()
   {
      return underlyingResource.getParent();
   }

   @Override
   public Resource<?> getChild(final String name)
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