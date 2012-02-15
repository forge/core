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
import java.util.Collections;
import java.util.List;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jface.text.Document;
import org.jboss.forge.parser.java.EnumConstant;
import org.jboss.forge.parser.java.JavaEnum;
import org.jboss.forge.parser.java.Member;
import org.jboss.forge.parser.java.SourceType;

/**
 * Represents a Java Source File containing an Enum Type.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class JavaEnumImpl extends AbstractJavaSource<JavaEnum> implements JavaEnum
{
   public JavaEnumImpl(final Document document, final CompilationUnit unit)
   {
      super(document, unit);
   }

   @Override
   public List<Member<JavaEnum, ?>> getMembers()
   {
      List<Member<JavaEnum, ?>> result = new ArrayList<Member<JavaEnum, ?>>();
      result.addAll(getFields());

      return result;
   }

   public List<EnumConstant<JavaEnum>> getFields()
   {
      List<EnumConstant<JavaEnum>> result = new ArrayList<EnumConstant<JavaEnum>>();

      for (Object o : (((EnumDeclaration) getBodyDeclaration()).enumConstants()))
      {
         EnumConstantDeclaration field = (EnumConstantDeclaration) o;
         result.add(new EnumConstantImpl<JavaEnum>((JavaEnum) this, field));
      }

      return Collections.unmodifiableList(result);
   }

   @Override
   @SuppressWarnings("unchecked")
   public EnumConstant<JavaEnum> addEnumConstant()
   {
      EnumConstantImpl<JavaEnum> enumConst = new EnumConstantImpl<JavaEnum>(this);
      getBodyDeclaration().bodyDeclarations().add(enumConst.getInternal());

      return enumConst;
   }

   @Override
   @SuppressWarnings("unchecked")
   public EnumConstant<JavaEnum> addEnumConstant(final String declaration)
   {
      EnumConstantImpl<JavaEnum> enumConst = new EnumConstantImpl<JavaEnum>(this, declaration);

      EnumDeclaration enumDeclaration = (EnumDeclaration) getBodyDeclaration();
      List<EnumConstantDeclaration> constants = enumDeclaration.enumConstants();
      constants.add((EnumConstantDeclaration) enumConst.getInternal());

      return enumConst;
   }

   @Override
   public EnumConstant<JavaEnum> getEnumConstant(String declaration)
   {
      for (EnumConstant<JavaEnum> enumConst : getFields())
      {
         if (enumConst.getName().equals(declaration))
         {
            return enumConst;
         }
      }
      return null;
   }

   protected JavaEnum updateTypeNames(final String newName)
   {
      return this;
   }

   @Override
   public SourceType getSourceType()
   {
      return SourceType.ENUM;
   }
}
