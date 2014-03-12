package org.jboss.forge.addon.scaffold.mock;

import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.resource.ResourceGenerator;

public class ScaffoldableResourceGenerator implements ResourceGenerator<ScaffoldableResource, Scaffoldable>
{
   @Override
   public boolean handles(Class<?> type, Object resource)
   {
      if (resource instanceof Scaffoldable)
      {
         return true;
      }
      return false;
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T extends Resource<Scaffoldable>> T getResource(ResourceFactory factory, Class<ScaffoldableResource> type,
            Scaffoldable resource)
   {
      return (T) new ScaffoldableResource(factory, resource);
   }

   @Override
   public <T extends Resource<Scaffoldable>> Class<?> getResourceType(ResourceFactory factory,
            Class<ScaffoldableResource> type, Scaffoldable resource)
   {
      return ScaffoldableResource.class;
   }
}
