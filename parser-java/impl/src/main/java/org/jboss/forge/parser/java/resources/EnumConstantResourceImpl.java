/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.parser.java.resources;

import java.util.Collections;
import java.util.List;

import org.jboss.forge.parser.java.EnumConstant;
import org.jboss.forge.parser.java.JavaEnum;
import org.jboss.forge.resource.Resource;
import org.jboss.forge.resource.ResourceFacet;
import org.jboss.forge.resource.ResourceFactory;
import org.jboss.forge.resource.VirtualResource;

public class EnumConstantResourceImpl extends VirtualResource<EnumConstant<JavaEnum>> implements EnumConstantResource
{
   private final EnumConstant<JavaEnum> enumConstant;

   public EnumConstantResourceImpl(final ResourceFactory factory, final Resource<?> parent,
            final EnumConstant<JavaEnum> enumConstant)
   {
      super(factory, parent);
      this.enumConstant = enumConstant;
   }

   @Override
   public Resource<EnumConstant<JavaEnum>> createFrom(final EnumConstant<JavaEnum> file)
   {
      throw new RuntimeException("not implemented");
   }

   @Override
   protected List<Resource<?>> doListResources()
   {
      return Collections.emptyList();
   }

   @Override
   public EnumConstant<JavaEnum> getUnderlyingResourceObject()
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
