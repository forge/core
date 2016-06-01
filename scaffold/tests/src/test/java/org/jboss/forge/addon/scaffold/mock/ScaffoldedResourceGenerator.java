/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.scaffold.mock;

import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.resource.ResourceGenerator;

public class ScaffoldedResourceGenerator implements ResourceGenerator<ScaffoldedResource, Scaffolded>
{
   @Override
   public boolean handles(Class<?> type, Object resource)
   {
      if (resource instanceof ScaffoldedResource)
      {
         return true;
      }
      return false;
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T extends Resource<Scaffolded>> T getResource(ResourceFactory factory, Class<ScaffoldedResource> type,
            Scaffolded resource)
   {
      return (T) new ScaffoldedResource(factory, resource);
   }

   @Override
   public <T extends Resource<Scaffolded>> Class<?> getResourceType(ResourceFactory factory,
            Class<ScaffoldedResource> type, Scaffolded resource)
   {
      return ScaffoldedResource.class;
   }
}
