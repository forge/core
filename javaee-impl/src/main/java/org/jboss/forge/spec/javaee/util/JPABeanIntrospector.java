package org.jboss.forge.spec.javaee.util;

import java.beans.Introspector;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Member;
import org.jboss.forge.parser.java.Method;
import org.jboss.forge.parser.java.Parameter;

public class JPABeanIntrospector
{

   private static final String GET_PREFIX = "get";
   private static final String SET_PREFIX = "set";
   private static final String IS_PREFIX = "is";

   private JavaClass entity;

   private List<Member<JavaClass, ?>> members;

   private Map<String, JPAProperty> propertyCache = new HashMap<String, JPAProperty>();

   public JPABeanIntrospector(final JavaClass entity)
   {
      this.entity = entity;
      this.members = this.entity.getMembers();
      locateProperties();
   }

   public List<JPAProperty> getProperties()
   {
      return new ArrayList<JPAProperty>(propertyCache.values());
   }

   private void locateProperties()
   {
      for (Member<JavaClass, ?> member : getEligibleMembers())
      {
         String memberName = member.getName();
         if (member instanceof Field)
         {
            Field<?> field = (Field<?>) member;
            createOrUpdateProperty(memberName, field, null, null);
         }
         else if (member instanceof Method)
         {
            @SuppressWarnings("unchecked")
            Method<JavaClass> method = (Method<JavaClass>) member;
            if (isAccessor(method))
            {
               String propertyName = propertyNameFromMethod(method);
               createOrUpdateProperty(propertyName, null, method, null);
            }
            else if (isMutator(method))
            {
               String propertyName = propertyNameFromMethod(method);
               createOrUpdateProperty(propertyName, null, null, method);
            }
         }
      }
   }

   private List<Member<JavaClass, ?>> getEligibleMembers()
   {
      List<Member<JavaClass, ?>> result = new ArrayList<Member<JavaClass, ?>>();
      for (Member<JavaClass, ?> member : members)
      {
         if (!member.isStatic())
         {
            result.add(member);
         }
      }
      return result;
   }

   private String propertyNameFromMethod(Method<JavaClass> method)
   {
      String methodName = method.getName();
      String qualifiedReturnType = method.getQualifiedReturnType();
      if (methodName.startsWith(GET_PREFIX))
      {
         return Introspector.decapitalize(methodName.substring(3));
      }
      else if (methodName.startsWith(IS_PREFIX)
               && (qualifiedReturnType.equals("boolean") || qualifiedReturnType.equals("java.lang.Boolean")))
      {
         return Introspector.decapitalize(methodName.substring(2));
      }
      else if (methodName.startsWith(SET_PREFIX))
      {
         return Introspector.decapitalize(methodName.substring(3));
      }
      else
      {
         return null;
      }
   }

   private JPAProperty createOrUpdateProperty(String name, Field<?> field, Method<JavaClass> accessor,
            Method<JavaClass> mutator)
   {
      JPAProperty property = propertyCache.get(name);
      if (property == null)
      {
         property = new JPAProperty(name);
         propertyCache.put(name, property);
      }
      if (field != null)
      {
         property.setActualField(field);
      }
      if (accessor != null)
      {
         property.setAccessor(accessor);
      }
      if (mutator != null)
      {
         property.setMutator(mutator);
      }
      return property;
   }

   private boolean isAccessor(Method<JavaClass> method)
   {
      String methodName = method.getName();
      String qualifiedReturnType = method.getQualifiedReturnType();
      List<Parameter<JavaClass>> parameters = method.getParameters();
      if (!method.isStatic() && methodName.startsWith(GET_PREFIX) && qualifiedReturnType != null
               && parameters.size() == 0)
      {
         return true;
      }
      if (methodName.startsWith(IS_PREFIX)
               && (qualifiedReturnType.equals("boolean") || qualifiedReturnType.equals("java.lang.Boolean"))
               && parameters.size() == 0)
      {
         return true;
      }
      return false;
   }

   private boolean isMutator(Method<JavaClass> method)
   {
      String methodName = method.getName();
      String qualifiedReturnType = method.getQualifiedReturnType();
      List<Parameter<JavaClass>> parameters = method.getParameters();
      if (!method.isStatic() && methodName.startsWith(SET_PREFIX) && qualifiedReturnType == null
               && parameters.size() == 1)
      {
         return true;
      }
      return false;
   }
}
