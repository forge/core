/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.parser.java.impl;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Modifier.ModifierKeyword;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jface.text.Document;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Method;
import org.jboss.forge.parser.java.SourceType;
import org.jboss.forge.parser.java.ast.ModifierAccessor;
import org.jboss.forge.parser.java.util.Types;

/**
 * Represents a Java Source File
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class JavaClassImpl extends AbstractJavaSourceMemberHolder<JavaClass> implements JavaClass
{
   private final ModifierAccessor modifiers = new ModifierAccessor();

   public JavaClassImpl(final Document document, final CompilationUnit unit)
   {
      super(document, unit);
   }

   @Override
   protected JavaClass updateTypeNames(final String newName)
   {
      for (Method<JavaClass> m : getMethods())
      {
         if (m.isConstructor())
         {
            m.setConstructor(false);
            m.setConstructor(true);
         }
      }
      return this;
   }

   /*
    * Type modifiers
    */
   @Override
   public boolean isAbstract()
   {
      return modifiers.hasModifier(getBodyDeclaration(), ModifierKeyword.ABSTRACT_KEYWORD);
   }

   @Override
   public JavaClass setAbstract(final boolean abstrct)
   {
      if (abstrct)
      {
         modifiers.addModifier(getBodyDeclaration(), ModifierKeyword.ABSTRACT_KEYWORD);
      }
      else
      {
         modifiers.removeModifier(getBodyDeclaration(), ModifierKeyword.ABSTRACT_KEYWORD);
      }
      return this;
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + ((toString() == null) ? 0 : unit.toString().hashCode());
      return result;
   }

   @Override
   public boolean equals(final Object obj)
   {
      return (this == obj)
               || ((obj != null) && (getClass() == obj.getClass()) && this.toString().equals(obj.toString()));
   }

   @Override
   public String getSuperType()
   {
      Object superType = getBodyDeclaration().getStructuralProperty(TypeDeclaration.SUPERCLASS_TYPE_PROPERTY);
      if (superType == null)
         superType = Object.class.getName();
      return resolveType(superType.toString());
   }

   @Override
   public JavaClass setSuperType(final JavaClass type)
   {
      return setSuperType(type.getQualifiedName());
   }

   @Override
   public JavaClass setSuperType(final Class<?> type)
   {
      if (type.isAnnotation() || type.isEnum() || type.isInterface() || type.isPrimitive())
      {
         throw new IllegalArgumentException("Super-type must be a Class type, but was [" + type.getName() + "]");
      }
      return setSuperType(type.getName());
   }

   @Override
   public JavaClass setSuperType(final String type)
   {
      SimpleType simpleType = unit.getAST().newSimpleType(unit.getAST().newSimpleName(Types.toSimpleName(type)));
      getBodyDeclaration().setStructuralProperty(TypeDeclaration.SUPERCLASS_TYPE_PROPERTY, simpleType);

      if (!hasImport(type) && Types.isQualified(type))
      {
         addImport(type);
      }
      
      return this;
   }

   @Override
   public SourceType getSourceType()
   {
      return SourceType.CLASS;
   }
}
