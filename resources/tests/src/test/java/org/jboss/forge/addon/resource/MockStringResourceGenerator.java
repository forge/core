/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.resource;

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
   public <T extends Resource<String>> Class<?> getResourceType(ResourceFactory factory,
            Class<MockStringResource> type, String resource)
   {
      return MockStringResource.class;
   }
}
