package org.jboss.forge.addon.scaffold.mock;

import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.resource.VirtualResource;

import java.util.List;

public class ScaffoldableResource extends VirtualResource<Scaffoldable>
{

   private Scaffoldable value;

   public ScaffoldableResource(ResourceFactory factory, Scaffoldable resource)
   {
      super(factory, null);
      this.value = resource;
   }

   protected ScaffoldableResource(ResourceFactory factory, Resource<?> parent)
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
   public Scaffoldable getUnderlyingResourceObject()
   {
      return value;
   }
}
