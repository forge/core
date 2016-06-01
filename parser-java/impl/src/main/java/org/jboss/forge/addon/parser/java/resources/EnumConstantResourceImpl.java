/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.parser.java.resources;

import java.util.Collections;
import java.util.List;

import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFacet;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.resource.VirtualResource;
import org.jboss.forge.roaster.model.EnumConstant;

public class EnumConstantResourceImpl extends VirtualResource<EnumConstant<?>> implements
         EnumConstantResource
{
   private final EnumConstant<?> enumConstant;

   public EnumConstantResourceImpl(final ResourceFactory factory, final Resource<?> parent,
            final EnumConstant<?> enumConstant)
   {
      super(factory, parent);
      this.enumConstant = enumConstant;
   }

   @Override
   public Resource<EnumConstant<?>> createFrom(final EnumConstant<?> file)
   {
      throw new RuntimeException("not implemented");
   }

   @Override
   protected List<Resource<?>> doListResources()
   {
      return Collections.emptyList();
   }

   @Override
   public EnumConstant<?> getUnderlyingResourceObject()
   {
      return enumConstant;
   }

   @Override
   public String getName()
   {
      return enumConstant.getName();
   }

   @Override
   public String toString()
   {
      return enumConstant.toString();
   }

   @Override
   public boolean delete() throws UnsupportedOperationException
   {
      throw new UnsupportedOperationException("Deleting Enum constants is not implemented.");
   }

   @Override
   public boolean delete(boolean recursive) throws UnsupportedOperationException
   {
      throw new UnsupportedOperationException("Deleting Enum constants is not implemented.");
   }

   @Override
   public boolean supports(ResourceFacet type)
   {
      return false;
   }

}
