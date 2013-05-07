/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.resources.java;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.FieldHolder;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.parser.java.Method;
import org.jboss.forge.parser.java.util.Refactory;
import org.jboss.forge.parser.java.util.Strings;
import org.jboss.forge.parser.java.util.Types;
import org.jboss.forge.resources.DeletionAware;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.resources.ResourceFlag;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class JavaFieldResource extends JavaMemberResource<Field<? extends JavaSource<?>>> implements
         DeletionAware
{
   private final Field<? extends JavaSource<?>> field;

   public JavaFieldResource(final JavaResource parent, final Field<? extends JavaSource<?>> field)
   {
      super(parent, field);
      this.field = field;

      setFlag(ResourceFlag.Leaf);
   }

   @Override
   public Resource<Field<? extends JavaSource<?>>> createFrom(final Field<? extends JavaSource<?>> file)
   {
      throw new RuntimeException("not implemented");
   }

   @Override
   protected List<Resource<?>> doListResources()
   {
      return Collections.emptyList();
   }

   @Override
   public Field<? extends JavaSource<?>> getUnderlyingResourceObject()
   {
      return field;
   }

   @Override
   public String getName()
   {
      return field.getName() + "::" + field.getType();
   }

   @Override
   public String toString()
   {
      return field.toString();
   }

   @Override
   @SuppressWarnings({ "unchecked", "rawtypes" })
   public boolean delete() throws UnsupportedOperationException
   {
      JavaSource<?> origin = field.getOrigin();
      if (origin instanceof FieldHolder)
      {
         ((FieldHolder) origin).removeField(field);
         if (!((FieldHolder) origin).hasField(field))
         {
            updateToString((JavaClass) origin);
            ((JavaResource) this.getParent()).setContents(origin.toString());
            return true;
         }
      }
      return false;
   }

   @Override
   public boolean delete(final boolean recursive) throws UnsupportedOperationException
   {
      return delete();
   }

   @Override
   public List<Resource<?>> getResources()
   {
      return null;
   }

   @Override
   public List<Resource<?>> getOptionalResources()
   {
      List<Resource<?>> result = new ArrayList<Resource<?>>();
      JavaClass entity = (JavaClass) field.getOrigin();
      String methodNameSuffix = Strings.capitalize(field.getName());
      // Condition to remove getField()
      if (entity.hasMethodSignature("get" + methodNameSuffix))
      {
         Method<JavaClass> method = entity.getMethod("get" + methodNameSuffix);
         result.add(new JavaMethodResource(this.getParent(), method));
      }
      // Condition to remove setField()
      if (entity.hasMethodSignature("set" + methodNameSuffix, field.getQualifiedType()))
      {
         Method<JavaClass> method = entity.getMethod("set" + methodNameSuffix, field.getQualifiedType());
         result.add(new JavaMethodResource(this.getParent(), method));
      }
      return result;
   }

   /**
    * Copied from FieldPlugin
    */
   private static void updateToString(final JavaClass targetEntity)
   {
      if (targetEntity.hasMethodSignature("toString"))
      {
         targetEntity.removeMethod(targetEntity.getMethod("toString"));
      }
      List<Field<JavaClass>> fields = new ArrayList<Field<JavaClass>>();
      for (Field<JavaClass> f : targetEntity.getFields())
      {
         if (!"id".equals(f.getName()) && !"version".equals(f.getName())
                  && (f.getTypeInspector().isPrimitive() || Types.isJavaLang(f.getType())))
         {
            fields.add(f);
         }
      }
      if (!fields.isEmpty())
      {
         Refactory.createToStringFromFields(targetEntity, fields);
      }
   }

}
