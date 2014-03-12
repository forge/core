package org.jboss.forge.addon.scaffold.mock;

import java.util.List;

import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.resource.VirtualResource;

public class ScaffoldedResource extends VirtualResource<Scaffolded>
{

   private Scaffolded value;

   public ScaffoldedResource(ResourceFactory factory, Scaffolded resource)
   {
      super(factory, null);
      this.value = resource;
   }

   protected ScaffoldedResource(ResourceFactory factory, Resource<?> parent)
   {
      super(factory, parent);
   }

   @Override
   protected List<Resource<?>> doListResources()
   {
      return null;
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
      return value.toString();
   }

   @Override
   public Scaffolded getUnderlyingResourceObject()
   {
      return value;
   }
}
