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

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.Annotation;
import org.jboss.forge.parser.java.AnnotationTarget;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.parser.java.ValuePair;
import org.jboss.forge.parser.java.util.Strings;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class AnnotationImpl<O extends JavaSource<O>, T> implements Annotation<O>
{
   private static final String DEFAULT_VALUE = "value";

   private AnnotationTarget<O, T> parent = null;
   private AST ast = null;
   private org.eclipse.jdt.core.dom.Annotation annotation;

   private enum AnnotationType
   {
      MARKER, SINGLE, NORMAL
   }

   private void init(final AnnotationTarget<O, T> parent)
   {
      this.parent = parent;
      ast = ((ASTNode) parent.getInternal()).getAST();
   }

   public AnnotationImpl(final AnnotationTarget<O, T> parent)
   {
      this(parent, AnnotationType.MARKER);
   }

   public AnnotationImpl(final AnnotationTarget<O, T> parent, final Object internal)
   {
      init(parent);
      this.annotation = (org.eclipse.jdt.core.dom.Annotation) internal;
   }

   public AnnotationImpl(final AnnotationTarget<O, T> parent, final AnnotationType type)
   {
      init(parent);
      switch (type)
      {
      case MARKER:
         this.annotation = ast.newMarkerAnnotation();
         break;
      case SINGLE:
         this.annotation = ast.newSingleMemberAnnotation();
         break;
      case NORMAL:
         this.annotation = ast.newNormalAnnotation();
         break;
      default:
         throw new IllegalArgumentException("Unknown annotation type: " + type);
      }
   }

   @Override
   public String getName()
   {
      return annotation.getTypeName().getFullyQualifiedName();
   }

   @Override
   public String getQualifiedName()
   {
      return parent.getOrigin().resolveType(getName());
   }

   @Override
   public String getLiteralValue() throws IllegalStateException
   {
      String result = null;
      if (isSingleValue())
      {
         SingleMemberAnnotation sm = (SingleMemberAnnotation) annotation;
         result = sm.getValue().toString();
      }
      else if (isNormal())
      {
         List<ValuePair> values = getValues();
         for (ValuePair pair : values)
         {
            String name = pair.getName();
            if (DEFAULT_VALUE.equals(name))
            {
               result = pair.getLiteralValue();
               break;
            }
         }
      }
      return result;
   }

   @Override
   public String getLiteralValue(final String name)
   {
      String result = null;
      if (isNormal())
      {
         for (Object v : ((NormalAnnotation) annotation).values())
         {
            if (v instanceof MemberValuePair)
            {
               MemberValuePair pair = (MemberValuePair) v;
               if (pair.getName().getFullyQualifiedName().equals(name))
               {
                  result = pair.getValue().toString();
                  break;
               }
            }
         }
      }
      else if (DEFAULT_VALUE.equals(name) && isSingleValue())
      {
         return getLiteralValue();
      }
      return result;
   }

   @Override
   public List<ValuePair> getValues()
   {
      List<ValuePair> result = new ArrayList<ValuePair>();
      if (isNormal())
      {
         for (Object v : ((NormalAnnotation) annotation).values())
         {
            if (v instanceof MemberValuePair)
            {
               MemberValuePair pair = (MemberValuePair) v;
               ValuePair temp = new ValuePairImpl(pair.getName().getFullyQualifiedName(), pair.getValue().toString());
               result.add(temp);
            }
         }
      }
      else if (isSingleValue())
      {
         result.add(new ValuePairImpl(DEFAULT_VALUE, getLiteralValue()));
      }
      return Collections.unmodifiableList(result);
   }

   @Override
   public String getStringValue() throws IllegalStateException
   {
      return Strings.unquote(getLiteralValue());
   }

   @Override
   public String getStringValue(final String name)
   {
      return Strings.unquote(getLiteralValue(name));
   }

   @Override
   public boolean isMarker()
   {
      return annotation.isMarkerAnnotation();
   }

   @Override
   public boolean isNormal()
   {
      return annotation.isNormalAnnotation();
   }

   @Override
   public boolean isSingleValue()
   {
      return annotation.isSingleMemberAnnotation();
   }

   @Override
   public Annotation<O> removeAllValues()
   {
      convertTo(AnnotationType.MARKER);
      return this;
   }

   @Override
   @SuppressWarnings("unchecked")
   public Annotation<O> removeValue(final String name)
   {
      if (annotation.isNormalAnnotation())
      {
         NormalAnnotation na = (NormalAnnotation) annotation;

         List<MemberValuePair> toBeRemoved = new ArrayList<MemberValuePair>();
         for (Object v : na.values())
         {
            if (v instanceof MemberValuePair)
            {
               MemberValuePair pair = (MemberValuePair) v;
               if (pair.getName().toString().equals(name))
               {
                  toBeRemoved.add(pair);
               }
            }
         }
         na.values().removeAll(toBeRemoved);

         if ((getLiteralValue() != null) && (getValues().size() == 1))
         {
            convertTo(AnnotationType.SINGLE);
         }
         else if (getValues().size() == 0)
         {
            convertTo(AnnotationType.MARKER);
         }
      }
      else if (annotation.isSingleMemberAnnotation())
      {
         removeAllValues();
      }
      return this;
   }

   @Override
   public Annotation<O> setName(final String className)
   {
      annotation.setTypeName(ast.newName(className));
      return this;
   }

   @Override
   public Annotation<O> setLiteralValue(final String value)
   {
      if (isMarker())
      {
         convertTo(AnnotationType.SINGLE);
      }

      if (isSingleValue())
      {
         SingleMemberAnnotation sa = (SingleMemberAnnotation) annotation;

         String stub = "@" + getName() + "(" + value + ") public class Stub { }";
         JavaClass temp = (JavaClass) JavaParser.parse(stub);

         SingleMemberAnnotation anno = (SingleMemberAnnotation) temp.getAnnotations().get(0).getInternal();

         Expression expression = anno.getValue();
         sa.setValue((Expression) ASTNode.copySubtree(ast, expression));
      }
      else
      {
         setLiteralValue(DEFAULT_VALUE, value);
      }

      return this;
   }

   @Override
   @SuppressWarnings("unchecked")
   public Annotation<O> setLiteralValue(final String name, final String value)
   {
      if (!isNormal() && !DEFAULT_VALUE.equals(name))
      {
         convertTo(AnnotationType.NORMAL);
      }
      else if (!isSingleValue() && !isNormal() && DEFAULT_VALUE.equals(name))
      {
         convertTo(AnnotationType.SINGLE);
         return setLiteralValue(value);
      }

      NormalAnnotation na = (NormalAnnotation) annotation;

      String stub = "@" + getName() + "(" + name + "=" + value + " ) public class Stub { }";
      JavaClass temp = (JavaClass) JavaParser.parse(stub);

      NormalAnnotation anno = (NormalAnnotation) temp.getAnnotations().get(0).getInternal();

      for (Object v : anno.values())
      {
         if (v instanceof MemberValuePair)
         {
            na.values().add(ASTNode.copySubtree(annotation.getAST(), (MemberValuePair) v));
         }
      }

      return this;
   }

   @Override
   public Annotation<O> setStringValue(final String value)
   {
      return setLiteralValue(Strings.enquote(value));
   }

   @Override
   public Annotation<O> setStringValue(final String name, final String value)
   {
      return setLiteralValue(name, Strings.enquote(value));
   }

   @Override
   public <E extends Enum<E>> E getEnumValue(final Class<E> type)
   {
      String literalValue = getLiteralValue();
      return convertLiteralToEnum(type, literalValue);
   }

   @Override
   public <E extends Enum<E>> E getEnumValue(final Class<E> type, final String name)
   {
      String literalValue = getLiteralValue(name);
      return convertLiteralToEnum(type, literalValue);
   }

   private <E extends Enum<E>> E convertLiteralToEnum(final Class<E> type, String literalValue)
   {
      E[] constants = type.getEnumConstants();

      for (E inst : constants)
      {
         String[] tokens = literalValue.split("\\.");
         if (tokens.length > 1)
         {
            literalValue = tokens[tokens.length - 1];
         }

         if (inst.name().equals(literalValue))
         {
            return inst;
         }
      }
      return null;
   }

   @Override
   public Annotation<O> setEnumValue(final String name, final Enum<?> value)
   {
      O origin = getOrigin();

      if (origin instanceof JavaSource)
      {
         JavaSource<?> source = origin;
         if (!source.hasImport(value.getDeclaringClass()))
         {
            source.addImport(value.getDeclaringClass());
         }
      }
      return setLiteralValue(name, value.getDeclaringClass().getSimpleName() + "." + value.name());
   }

   @Override
   public Annotation<O> setEnumValue(final Enum<?>... values)
   {
      O origin = getOrigin();
      
      String result = new String();// = "{";
      
      if(values.length > 1) {
	  result = "{";
      }

      if (origin instanceof JavaSource)
      {
         JavaSource<?> source = origin;
         
         for(Enum<?> value : values) {
             if (!source.hasImport(value.getDeclaringClass()))
             {
                source.addImport(value.getDeclaringClass());
             }
             
             result = result.concat(value.getDeclaringClass().getSimpleName() + "." + value.name() + ",");
         }
         
         result = result.substring(0, result.length()-1);
         
         if(values.length > 1) {
             result = result.concat("}");
         }
      }
      
      return setLiteralValue(result);
   }

   /*
    * Shared interface methods.
    */
   @Override
   public O getOrigin()
   {
      return parent.getOrigin();
   }

   @Override
   public Object getInternal()
   {
      return annotation;
   }

   @Override
   public String toString()
   {
      return annotation.toString();
   }

   @SuppressWarnings("unchecked")
   private void convertTo(final AnnotationType type)
   {
      BodyDeclaration node = (BodyDeclaration) annotation.getParent();
      String value = this.getLiteralValue();

      for (Object o : node.modifiers())
      {
         if (o.equals(annotation))
         {
            node.modifiers().remove(annotation);
            Annotation<O> na = new AnnotationImpl<O, T>(parent, type);
            na.setName(getName());
            annotation = (org.eclipse.jdt.core.dom.Annotation) na.getInternal();
            node.modifiers().add(annotation);
            break;
         }
      }

      if (!AnnotationType.MARKER.equals(type) && (value != null))
      {
         setLiteralValue(value);
      }
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + ((annotation == null) ? 0 : annotation.hashCode());
      return result;
   }

   @Override
   public boolean equals(final Object obj)
   {
      if (this == obj)
      {
         return true;
      }
      if (obj == null)
      {
         return false;
      }
      if (getClass() != obj.getClass())
      {
         return false;
      }
      AnnotationImpl<?, ?> other = (AnnotationImpl<?, ?>) obj;
      if (annotation == null)
      {
         if (other.annotation != null)
         {
            return false;
         }
      }
      else if (!annotation.equals(other.annotation))
      {
         return false;
      }
      return true;
   }

}
