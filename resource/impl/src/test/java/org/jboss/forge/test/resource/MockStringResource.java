package org.jboss.forge.test.resource;

import java.util.List;

import org.jboss.forge.addon.facets.Facet;
import org.jboss.forge.resource.Resource;
import org.jboss.forge.resource.ResourceFactory;
import org.jboss.forge.resource.VirtualResource;

public class MockStringResource extends VirtualResource<String>
{
   private String value;

   public MockStringResource(ResourceFactory factory, String resource)
   {
      super(factory, null);
      this.value = resource;
   }

   protected MockStringResource(ResourceFactory factory, Resource<?> parent)
   {
      super(factory, parent);
   }

   @Override
   public boolean delete() throws UnsupportedOperationException
   {
      return false;
   }

   @Override
   public boolean delete(boolean recursive) throws UnsupportedOperationException
   {
      return false;
   }

   @Override
   public String getName()
   {
      return value;
   }

   @Override
   public String getUnderlyingResourceObject()
   {
      return value;
   }

   @Override
   public boolean supports(Class<? extends Facet<?>> type)
   {
      return false;
   }

   @Override
   protected List<Resource<?>> doListResources()
   {
      return null;
   }

}
