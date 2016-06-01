/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.scaffold.spi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jboss.forge.addon.resource.Resource;

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
