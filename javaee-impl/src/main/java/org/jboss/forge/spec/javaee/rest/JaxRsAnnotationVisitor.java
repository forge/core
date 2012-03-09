package org.jboss.forge.spec.javaee.rest;

import java.io.FileNotFoundException;

import javax.ws.rs.ApplicationPath;

import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.resources.java.JavaResourceVisitor;

public class JaxRsAnnotationVisitor implements JavaResourceVisitor
{
   private String applicationPath;
   private JavaClass clazz;
   private JavaResource javaResource;
   
   @Override
   public void visit(JavaResource javaResource)
   {
      if (!isFound())
      {
         try
         {
            JavaSource<?> source = javaResource.getJavaSource();
            if (source.isClass())
            {
               JavaClass clazz = (JavaClass) source;
               if (clazz.hasAnnotation(ApplicationPath.class))
               {
                  this.applicationPath = clazz.getAnnotation(ApplicationPath.class).getStringValue();
                  this.clazz = clazz;
                  this.javaResource = javaResource;
               }
            }
         }
         catch (FileNotFoundException e)
         {
            e.printStackTrace();
         }
      }
   }

   public boolean isFound()
   {
      return clazz != null;
   }

   public String getApplicationPath()
   {
      return applicationPath;
   }

   public JavaResource getJavaResource()
   {
      return javaResource;
   }

   public JavaClass getSource()
   {
      return clazz;
   }
}
