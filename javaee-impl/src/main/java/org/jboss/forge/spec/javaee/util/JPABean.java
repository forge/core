package org.jboss.forge.spec.javaee.util;

import java.util.List;

import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Method;
import org.jboss.forge.parser.java.Parameter;
import org.jboss.forge.parser.java.util.Strings;

public class JPABean
{

   private static final String GETTER_PREFIX = "get";

   private static final String SETTER_PREFIX = "set";

   private JavaClass entity;

   public JPABean(JavaClass entity)
   {
      this.entity = entity;
   }

   public boolean isWritable(Field<?> field)
   {
      for (Method<?> method : entity.getMethods())
      {
         String methodName = method.getName();
         String fieldName = field.getName();
         String fieldType = field.getType();
         String setterName = SETTER_PREFIX + Strings.capitalize(fieldName);
         List<?> parameters = method.getParameters();
         int noOfParams = parameters.size();

         boolean isReturnTypeVoid = method.getReturnType() == null;
         boolean isMatchingSetterName = methodName.equals(setterName);
         boolean isMatchingParameterType = (noOfParams == 1 && ((Parameter<?>) parameters.get(0)).getType().equals(
                  fieldType));

         if (isMatchingSetterName && isReturnTypeVoid && isMatchingParameterType)
         {
            return true;
         }
      }
      return false;
   }

   public boolean isReadable(Field<?> field)
   {
      for (Method<?> method : entity.getMethods())
      {
         String methodName = method.getName();
         String fieldName = field.getName();
         String qualifiedFieldType = field.getQualifiedType();
         String getterName = GETTER_PREFIX + Strings.capitalize(fieldName);
         int noOfParams = method.getParameters().size();

         boolean isReturnTypeSameAsField = qualifiedFieldType.equals(method.getQualifiedReturnType());
         boolean isMatchingGetterName = methodName.equals(getterName);
         boolean isMatchingParameterType = (noOfParams == 0);

         if (isMatchingGetterName && isReturnTypeSameAsField && isMatchingParameterType)
         {
            return true;
         }
      }
      return false;
   }
}
