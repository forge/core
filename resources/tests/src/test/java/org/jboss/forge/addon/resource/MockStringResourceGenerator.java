package org.jboss.forge.addon.resource;

import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.resource.ResourceGenerator;
import org.jboss.forge.container.services.Exported;

@Exported
public class MockStringResourceGenerator implements ResourceGenerator<MockStringResource, String>
{
   @Override
   public boolean handles(Class<?> type, Object resource)
   {
      if (resource instanceof String)
      {
         if (((String) resource).matches(".*valid.*"))
            return true;
      }
      return false;
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T extends Resource<String>> T getResource(ResourceFactory factory, Class<MockStringResource> type,
            String resource)
   {
      return (T) new MockStringResource(factory, resource);
   }

   @Override
   public <T extends Resource<String>> Class<?> getResourceType(Class<MockStringResource> type, String resource)
   {
      return MockStringResource.class;
   }
}
