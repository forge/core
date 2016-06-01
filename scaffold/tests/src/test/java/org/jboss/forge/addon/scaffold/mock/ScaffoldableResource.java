/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.scaffold.mock;

import java.util.List;

import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.resource.VirtualResource;

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
