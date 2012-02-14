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

import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.Annotation;
import org.jboss.forge.parser.java.EnumConstant;
import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.JavaEnum;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.parser.java.Member;
import org.jboss.forge.parser.java.Visibility;

public class EnumConstantImpl<O extends JavaSource<O>> implements EnumConstant<O>
{
   private O parent;
   private AST ast;
   private EnumConstantDeclaration enumConstant;

   private void init(final O parent)
   {
      this.parent = parent;
      this.ast = ((ASTNode)parent.getInternal()).getAST();
   }
   
   public EnumConstantImpl(final O parent) {
      init(parent);
      this.enumConstant = ast.newEnumConstantDeclaration();
   }
   
   public EnumConstantImpl(final O parent, final String declaration)
   {
      init(parent);

      String stub = "public enum Stub { " + declaration + " }";
      JavaEnum temp = (JavaEnum) JavaParser.parse(stub);
      List<Member<JavaEnum, ?>> fields = temp.getMembers();
      EnumConstantDeclaration newField = (EnumConstantDeclaration) fields.get(0).getInternal();
      EnumConstantDeclaration subtree = (EnumConstantDeclaration) ASTNode.copySubtree(ast, newField);
      this.enumConstant = subtree;
   }
   
   public EnumConstantImpl(final O parent, final Object internal)
   {
      init(parent);
      this.enumConstant = (EnumConstantDeclaration) internal;
   }

   @Override
   public String getName()
   {
      return this.enumConstant.getName().getFullyQualifiedName();
   }

   @Override
   public EnumConstant<O> setName(String name)
   {
      this.enumConstant.setName(ast.newSimpleName(name));
      return this;
   }

   @Override
   public String getType()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public String getQualifiedType()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public boolean isType(Class<?> type)
   {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public boolean isType(String type)
   {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public EnumConstant<O> setType(Class<?> clazz)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public EnumConstant<O> setType(String type)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public EnumConstant<O> setType(JavaSource<?> entity)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public String getStringInitializer()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public String getLiteralInitializer()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public EnumConstant<O> setLiteralInitializer(String value)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public EnumConstant<O> setStringInitializer(String value)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Object getInternal()
   {
      return enumConstant;
   }

   @Override
   public boolean isFinal()
   {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public EnumConstant<O> setFinal(boolean finl)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public boolean isStatic()
   {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public EnumConstant<O> setStatic(boolean statc)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public boolean isPackagePrivate()
   {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public EnumConstant<O> setPackagePrivate()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public boolean isPublic()
   {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public EnumConstant<O> setPublic()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public boolean isPrivate()
   {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public EnumConstant<O> setPrivate()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public boolean isProtected()
   {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public EnumConstant<O> setProtected()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Visibility getVisibility()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public EnumConstant<O> setVisibility(Visibility scope)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Annotation<O> addAnnotation()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Annotation<O> addAnnotation(Class<? extends java.lang.annotation.Annotation> type)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Annotation<O> addAnnotation(String className)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public List<Annotation<O>> getAnnotations()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public boolean hasAnnotation(Class<? extends java.lang.annotation.Annotation> type)
   {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public boolean hasAnnotation(String type)
   {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public Annotation<O> getAnnotation(Class<? extends java.lang.annotation.Annotation> type)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Annotation<O> getAnnotation(String type)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public EnumConstant<O> removeAnnotation(Annotation<O> annotation)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public O getOrigin()
   {
      // TODO Auto-generated method stub
      return null;
   }
}
