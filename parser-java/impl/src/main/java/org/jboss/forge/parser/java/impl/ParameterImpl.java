/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.parser.java.impl;

import java.lang.reflect.Field;

import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.parser.java.Parameter;
import org.jboss.forge.parser.java.Type;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class ParameterImpl implements Parameter
{
   private final JavaSource<?> parent;
   private final VariableDeclaration param;

   public ParameterImpl(final JavaSource<?> parent, final Object internal)
   {
      this.parent = parent;
      this.param = (VariableDeclaration) internal;
   }

   @Override
   public String toString()
   {
      return param.toString();
   }

   @Override
   public String getName()
   {
      SimpleName name = param.getName();
      if (name != null)
      {
         return name.toString();
      }
      return "";
   }

   @Override
   public String getType()
   {

      return getTypeObject().toString();
   }

   @Override
   @SuppressWarnings({ "rawtypes", "unchecked" })
   public Type<?> getTypeInspector()
   {
      return new TypeImpl(parent, getTypeObject());
   }

   private Object getTypeObject()
   {
      Object type;

      try
      {
         // FIXME there *must* be a better way of doing this
         Class<? extends VariableDeclaration> clazz = param.getClass();
         Field field = clazz.getDeclaredField("type");
         field.setAccessible(true);
         type = field.get(param);
         field.setAccessible(false);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
      return type;
   }

}
