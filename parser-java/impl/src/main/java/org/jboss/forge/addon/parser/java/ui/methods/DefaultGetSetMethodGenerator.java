/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.parser.java.ui.methods;

import org.jboss.forge.roaster.model.Type;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.MethodSource;
import org.jboss.forge.roaster.model.source.PropertySource;

public class DefaultGetSetMethodGenerator implements GetSetMethodGenerator
{
   @Override
   public MethodSource<JavaClassSource> createAccessor(PropertySource<JavaClassSource> property)
   {
      return property.createAccessor();
   }

   @Override
   public MethodSource<JavaClassSource> createMutator(PropertySource<JavaClassSource> property)
   {
      return property.createMutator();
   }

   @Override
   public boolean isCorrectAccessor(MethodSource<JavaClassSource> method, PropertySource<JavaClassSource> property)
   {
      String returnType = method.getReturnType().getName();
      return returnType.equals(property.getType().getName());
   }

   @Override
   public boolean isCorrectMutator(MethodSource<JavaClassSource> method, PropertySource<JavaClassSource> property)
   {
      Type<JavaClassSource> returnType = method.getReturnType();
      return returnType.getName().equals("void");

   }

}
