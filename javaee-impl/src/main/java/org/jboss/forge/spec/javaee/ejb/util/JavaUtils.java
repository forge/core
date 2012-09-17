package org.jboss.forge.spec.javaee.ejb.util;

import java.io.FileNotFoundException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.parser.java.util.Types;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.resources.java.JavaMethodResource;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.shell.Shell;

public class JavaUtils
{

   public static JavaClass getJavaClassFrom(Resource<?> resource)
            throws FileNotFoundException
   {
      JavaSource<?> source = ((JavaResource) resource).getJavaSource();
      if (!source.isClass())
      {
         throw new IllegalStateException(
                  "Current resource is not a JavaClass!");
      }
      return (JavaClass) source;
   }

   public static Field<JavaClass> addFieldTo(JavaClass targetEjb,
            String fieldType, String fieldName,
            Class<? extends java.lang.annotation.Annotation> annotation,
            Project project, Shell shell) throws FileNotFoundException
   {
      if (targetEjb.hasField(fieldName))
      {
         throw new IllegalStateException("Ejb already has a field named ["
                  + fieldName + "]");
      }
      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      Field<JavaClass> field = targetEjb.addField();
      field.setName(fieldName).setPrivate()
               .setType(Types.toSimpleName(fieldType))
               .addAnnotation(annotation);
      targetEjb.addImport(fieldType);
      java.saveJavaSource(targetEjb);
      shell.println("Added field to " + targetEjb.getQualifiedName() + ": "
               + field);

      return field;
   }

   public static void addAllMethodsTo(JavaClass javaClass,
            String interfaceClass, Shell shell)
   {
      javaClass.addInterface(interfaceClass);
      for (Method method : getMethods(interfaceClass, shell))
      {
         org.jboss.forge.parser.java.Method<JavaClass> methodJavaClass = javaClass
                  .addMethod();
         addSingleMethod(methodJavaClass, method, javaClass);
      }
   }

   public static void addAbstractMethodsTo(JavaClass javaClass,
            String interfaceClass, Shell shell)
   {
      javaClass.addInterface(interfaceClass);
      for (Method method : getMethods(interfaceClass, shell))
      {
         if (Modifier.isAbstract(method.getModifiers()))
         {
            org.jboss.forge.parser.java.Method<JavaClass> methodJavaClass = javaClass
                     .addMethod();
            addSingleMethod(methodJavaClass, method, javaClass);
         }
      }
   }

   private static Method[] getMethods(String interfaceClass, Shell shell)
   {
      Class clazz = null;
      try
      {
         clazz = Class.forName(interfaceClass);
      }
      catch (ClassNotFoundException e)
      {
         shell.println("Exception: " + e);
      }
      return clazz.getDeclaredMethods();
   }

   private static void addSingleMethod(
            org.jboss.forge.parser.java.Method<JavaClass> methodJavaClass,
            Method method, JavaClass javaClass)
   {
      methodJavaClass.setReturnType(method.getReturnType()).setName(
               method.getName());
      if (void.class.equals(method.getReturnType()))
      {
         methodJavaClass.setBody("");
      }
      else if (!method.getReturnType().isPrimitive())
      {
         javaClass.addImport(method.getReturnType());
         methodJavaClass.setBody(" return null;");
      }
      else
      {
         // byte,short,int,long,float,double,boolean,char
         if (method.getReturnType().equals(byte.class)
                  || method.getReturnType().equals(short.class)
                  || method.getReturnType().equals(int.class)
                  || method.getReturnType().equals(long.class)
                  || method.getReturnType().equals(float.class)
                  || method.getReturnType().equals(double.class)
                  || method.getReturnType().equals(char.class))
         {
            methodJavaClass.setBody(" return 0;");
         }
         else if (method.getReturnType().equals(boolean.class))
         {
            methodJavaClass.setBody(" return false;");
         }
      }
      if (Modifier.isPublic(method.getModifiers()))
      {
         methodJavaClass.setPublic();
      }
      else
      {
         methodJavaClass.setPrivate();
      }
      Class<?>[] params = method.getParameterTypes();
      if (params != null && params.length > 0)
      {
         int i = 0;
         StringBuffer sb = new StringBuffer();
         for (Class<?> class1 : params)
         {
            sb.append("," + class1.getName() + " arg" + i);
            i++;
            if (!javaClass.getInterfaces().contains(class1))
            {
               javaClass.addImport(class1);
            }
         }
         methodJavaClass.setParameters(sb.toString().substring(1));
      }
   }
}
