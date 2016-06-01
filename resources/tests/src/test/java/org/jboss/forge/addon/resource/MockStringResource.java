/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.resource;

import java.util.List;

public class MockStringResource extends VirtualResource<String>
{
   private String value;

   public MockStringResource(ResourceFactory factory, String resource)
   {
      super(factory, null);
      this.value = resource;
   }

   protected MockStringResource(ResourceFactory factory, Resource<?> parent)
   {
      super(factory, parent);
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
      return value;
   }

   @Override
   public String getUnderlyingResourceObject()
   {
      return value;
   }

   @Override
   public boolean supports(ResourceFacet type)
   {
      return false;
   }

   @Override
   protected List<Resource<?>> doListResources()
   {
      return null;
   }

}
