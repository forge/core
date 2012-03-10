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
import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.EnumConstant;
import org.jboss.forge.parser.java.JavaEnum;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.parser.java.Member;

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
      List<EnumConstant<JavaEnum>> constants = temp.getEnumConstants();
      EnumConstantDeclaration newField = (EnumConstantDeclaration) constants.get(0).getInternal();
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
   public Object getInternal()
   {
      return enumConstant;
   }

   @Override
   public O getOrigin()
   {
      return parent;
   }
}
