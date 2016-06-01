/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.parser.java.ui.methods;

import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.MethodSource;
import org.jboss.forge.roaster.model.source.PropertySource;

public class BuilderGetSetMethodGenerator extends DefaultGetSetMethodGenerator
{

   @Override
   public MethodSource<JavaClassSource> createMutator(PropertySource<JavaClassSource> property)
   {
      MethodSource<JavaClassSource> method = super.createMutator(property);
      final String body = String.format("this.%1$s = %1$s; return this;", property.getName());
      return method.setBody(body).setReturnType(property.getOrigin());
   }
   
   @Override
   public boolean isCorrectMutator(MethodSource<JavaClassSource> method, PropertySource<JavaClassSource> property)
   {
      String returnType = method.getReturnType().getName();
      String className = property.getOrigin().getName();
      return className.equals(returnType);
   }

}
