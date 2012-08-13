/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.resources.enumtype;

import java.util.Collections;
import java.util.List;

import org.jboss.forge.parser.java.EnumConstant;
import org.jboss.forge.parser.java.JavaEnum;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.resources.ResourceFlag;
import org.jboss.forge.resources.VirtualResource;

public class EnumConstantResource extends VirtualResource<EnumConstant<JavaEnum>>
{
   private final EnumConstant<JavaEnum> enumConstant;

   public EnumConstantResource(final Resource<?> parent, final EnumConstant<JavaEnum> enumConstant)
   {
      super(parent);
      this.enumConstant = enumConstant;
      setFlag(ResourceFlag.Leaf);
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

}
