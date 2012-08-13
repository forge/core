/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.parser.java.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.jboss.forge.parser.java.Annotation;
import org.jboss.forge.parser.java.AnnotationTarget;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.parser.java.impl.AnnotationImpl;
import org.jboss.forge.parser.java.util.Types;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class AnnotationAccessor<O extends JavaSource<O>, T>
{

   @SuppressWarnings("unchecked")
   public Annotation<O> addAnnotation(final AnnotationTarget<O, T> target, final BodyDeclaration body)
   {
      Annotation<O> annotation = new AnnotationImpl<O, T>(target);
      body.modifiers().add(0, annotation.getInternal());
      return annotation;
   }

   public Annotation<O> addAnnotation(final AnnotationTarget<O, T> target, final BodyDeclaration body,
            final Class<?> clazz)
   {
      return addAnnotation(target, body, clazz.getName());
   }

   public Annotation<O> addAnnotation(final AnnotationTarget<O, T> target, final BodyDeclaration body,
            final String className)
   {
      if (!target.getOrigin().hasImport(className) && Types.isQualified(className))
      {
         target.getOrigin().addImport(className);
      }
      return addAnnotation(target, body).setName(Types.toSimpleName(className));
   }

   public List<Annotation<O>> getAnnotations(final AnnotationTarget<O, T> target, final BodyDeclaration body)
   {
      List<Annotation<O>> result = new ArrayList<Annotation<O>>();

      List<?> modifiers = body.modifiers();
      for (Object object : modifiers)
      {
         if (object instanceof org.eclipse.jdt.core.dom.Annotation)
         {
            Annotation<O> annotation = new AnnotationImpl<O, T>(target, object);
            result.add(annotation);
         }
      }

      return Collections.unmodifiableList(result);
   }

   public <E extends AnnotationTarget<O, T>> E removeAnnotation(final E target, final BodyDeclaration body,
            final Annotation<O> annotation)
   {
      List<?> modifiers = body.modifiers();
      for (Object object : modifiers)
      {
         if (object.equals(annotation.getInternal()))
         {
            modifiers.remove(object);
            break;
         }
      }
      return target;
   }

   public <E extends AnnotationTarget<O, T>> boolean hasAnnotation(final E target, final BodyDeclaration body,
            final String type)
   {
      List<?> modifiers = body.modifiers();
      for (Object object : modifiers)
      {
         if (object instanceof org.eclipse.jdt.core.dom.Annotation)
         {
            Annotation<O> annotation = new AnnotationImpl<O, T>(target, object);
            String annotationType = annotation.getName();
            if (Types.areEquivalent(type, annotationType))
            {
               return true;
            }
         }
      }
      return false;
   }

   public Annotation<O> getAnnotation(final AnnotationTarget<O, T> target, final BodyDeclaration body,
            final Class<? extends java.lang.annotation.Annotation> type)
   {
      Annotation<O> result = null;
      if (type != null)
      {
         result = getAnnotation(target, body, type.getName());
      }
      return result;
   }

   public Annotation<O> getAnnotation(final AnnotationTarget<O, T> target, final BodyDeclaration body, final String type)
   {
      List<Annotation<O>> annotations = getAnnotations(target, body);
      for (Annotation<O> annotation : annotations)
      {
         if (Types.areEquivalent(type, annotation.getName()))
         {
            return annotation;
         }
      }
      return null;
   }
}
