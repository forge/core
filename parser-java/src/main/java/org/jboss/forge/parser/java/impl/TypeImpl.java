/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.forge.parser.java.impl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.parser.java.Method;
import org.jboss.forge.parser.java.Type;
import org.jboss.forge.parser.java.util.Types;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class TypeImpl<O extends JavaSource<O>> implements Type<O>
{
   private O origin = null;
   private final Type<O> parent;

   @SuppressWarnings("unused")
   private AST ast = null;

   private CompilationUnit cu = null;
   private final org.eclipse.jdt.core.dom.Type type;

   private void init(final O origin)
   {
      this.origin = origin;
      cu = (CompilationUnit) origin.getInternal();
      ast = cu.getAST();
   }

   public TypeImpl(final O origin, final Object internal)
   {
      init(origin);
      type = (org.eclipse.jdt.core.dom.Type) internal;
      parent = null;
   }

   public TypeImpl(final O origin, final Type<O> parent, final String type)
   {
      init(origin);
      this.parent = parent;

      String stub = "public class Stub { private " + type + " getType(){return null;} }";
      JavaClass temp = (JavaClass) JavaParser.parse(stub);
      List<Method<JavaClass>> methods = temp.getMethods();
      MethodDeclaration newMethod = (MethodDeclaration) methods.get(0).getInternal();
      org.eclipse.jdt.core.dom.Type subtree = (org.eclipse.jdt.core.dom.Type) ASTNode.copySubtree(cu.getAST(),
               newMethod.getReturnType2());
      this.type = subtree;
   }

   public TypeImpl(final O origin, final Type<O> parent, final Object internal)
   {
      init(origin);
      this.parent = parent;
      type = (org.eclipse.jdt.core.dom.Type) internal;
   }

   @Override
   public O getOrigin()
   {
      return origin;
   }

   @Override
   @SuppressWarnings("unchecked")
   public List<Type<O>> getTypeArguments()
   {
      List<Type<O>> result = new ArrayList<Type<O>>();
      org.eclipse.jdt.core.dom.Type type = this.type;

      if (type instanceof ArrayType)
      {
         type = ((ArrayType) type).getComponentType();
      }

      if (type instanceof ParameterizedType)
      {
         List<org.eclipse.jdt.core.dom.Type> arguments = ((ParameterizedType) type).typeArguments();
         for (org.eclipse.jdt.core.dom.Type t : arguments) {
            result.add(new TypeImpl<O>(origin, this, t));
         }
      }
      return result;
   }

   @Override
   public boolean isArray()
   {
      return type.isArrayType();
   }

   @Override
   public boolean isParameterized()
   {
      if (type instanceof ArrayType)
      {
         return ((ArrayType) type).getComponentType().isParameterizedType();
      }
      return type.isParameterizedType();
   }

   @Override
   public boolean isPrimitive()
   {
      if (type instanceof ArrayType)
      {
         return ((ArrayType) type).getComponentType().isPrimitiveType();
      }
      return type.isPrimitiveType();
   }

   @Override
   public boolean isQualified()
   {
      if (type instanceof ArrayType)
      {
         return ((ArrayType) type).getComponentType().isQualifiedType();
      }
      return type.isQualifiedType();
   }

   @Override
   public boolean isWildcard()
   {
      if (type instanceof ArrayType)
      {
         return ((ArrayType) type).getComponentType().isWildcardType();
      }
      return type.isWildcardType();
   }

   @Override
   public String getName()
   {
      String result = type.toString();
      return Types.stripGenerics(result);
   }

   @Override
   public String getQualifiedName()
   {
      String result = type.toString();
      return origin.resolveType(result);
   }

   @Override
   public Type<O> getParentType()
   {
      return parent;
   }

   @Override
   public String toString()
   {
      return type.toString();
   }

}
