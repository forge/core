package org.jboss.forge.addon.parser.json.resource;

import java.io.File;

import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.resource.ResourceGenerator;

public class JsonResourceGenerator implements ResourceGenerator<JsonResource, File>
{
   @Override
   public boolean handles(Class<?> type, Object resource)
   {
      if (resource instanceof File && ((File) resource).getName().endsWith(".json"))
      {
         return true;
      }
      return false;
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T extends Resource<File>> T getResource(ResourceFactory factory, Class<JsonResource> type, File resource)
   {
      return (T) new JsonResourceImpl(factory, resource);
   }

   @Override
   public <T extends Resource<File>> Class<?> getResourceType(ResourceFactory factory, Class<JsonResource> type,
            File resource)
   {
      return JsonResource.class;
   }
}
