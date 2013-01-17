package org.jboss.forge.test.resource;

import org.jboss.forge.container.services.Exported;
import org.jboss.forge.resource.Resource;
import org.jboss.forge.resource.ResourceFactory;
import org.jboss.forge.resource.ResourceGenerator;

@Exported
public class MockStringResourceGenerator implements ResourceGenerator<String>
{
   @Override
   public boolean handles(Object resource)
   {
      if (resource instanceof String)
      {
         if (((String) resource).matches(".*valid.*"))
            return true;
      }
      return false;
   }

   @Override
   public Class<? extends Resource<String>> getResourceType(String resource)
   {
      return MockStringResource.class;
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T extends Resource<String>> T getResource(ResourceFactory factory, String resource)
   {
      return (T) new MockStringResource(factory, resource);
   }
}
