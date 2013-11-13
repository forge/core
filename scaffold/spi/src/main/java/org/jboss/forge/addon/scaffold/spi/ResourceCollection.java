package org.jboss.forge.addon.scaffold.spi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ResourceCollection
{
   private List resources = new ArrayList();

   public Collection<?> getResources()
   {
      return resources;
   }

   public void addToCollection(Object resource)
   {
      resources.add(resource);
   }

}
