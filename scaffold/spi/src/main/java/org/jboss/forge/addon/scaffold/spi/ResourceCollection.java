package org.jboss.forge.addon.scaffold.spi;

import org.jboss.forge.addon.resource.Resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ResourceCollection
{
   private List<Resource<?>> resources = new ArrayList<Resource<?>>();

   public Collection<Resource<?>> getResources()
   {
      return resources;
   }

   public void addToCollection(Resource<?> resource)
   {
      resources.add(resource);
   }

}
