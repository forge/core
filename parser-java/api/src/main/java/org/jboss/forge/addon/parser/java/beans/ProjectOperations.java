/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.parser.java.beans;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.parser.java.resources.JavaResourceVisitor;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.resource.ResourceException;
import org.jboss.forge.addon.resource.visit.VisitContext;
import org.jboss.forge.roaster.model.source.JavaAnnotationSource;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.JavaEnumSource;
import org.jboss.forge.roaster.model.source.JavaInterfaceSource;
import org.jboss.forge.roaster.model.source.JavaSource;

/**
 * Operations related to java elements in a {@link Project}
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class ProjectOperations
{
   /**
    * Returns all the {@link JavaInterfaceSource} objects from the given {@link Project}
    */
   public List<JavaResource> getProjectInterfaces(Project project)
   {
      final List<JavaResource> classes = new ArrayList<>();
      if (project != null)
      {
         project.getFacet(JavaSourceFacet.class).visitJavaSources(new JavaInterfaceSourceVisitor(classes));
      }
      return classes;
   }

   /**
    * Returns all the {@link JavaClassSource} objects from the given {@link Project}
    */
   public List<JavaResource> getProjectClasses(Project project)
   {
      final List<JavaResource> classes = new ArrayList<>();
      if (project != null)
      {
         project.getFacet(JavaSourceFacet.class).visitJavaSources(new JavaClassSourceVisitor(classes));
      }
      return classes;
   }

   /**
    * Returns all the {@link JavaAnnotationSource} objects from the given {@link Project}
    */
   public List<JavaResource> getProjectAnnotations(Project project)
   {
      final List<JavaResource> classes = new ArrayList<>();
      if (project != null)
      {
         project.getFacet(JavaSourceFacet.class).visitJavaSources(new JavaAnnotationsSourceVisitor(classes));
      }
      return classes;
   }

   /**
    * Returns all the {@link JavaEnumSource} objects from the given {@link Project}
    */
   public List<JavaResource> getProjectEnums(Project project)
   {
      final List<JavaResource> enums = new ArrayList<>();
      if (project != null)
      {
         project.getFacet(JavaSourceFacet.class).visitJavaSources(new JavaResourceVisitor()
         {
            @Override
            public void visit(VisitContext context, JavaResource resource)
            {
               try
               {
                  JavaSource<?> javaSource = resource.getJavaType();
                  if (javaSource.isEnum())
                  {
                     enums.add(resource);
                  }
               }
               catch (ResourceException | FileNotFoundException e)
               {
                  // ignore
               }
            }
         });
      }
      return enums;
   }

   private static class JavaClassSourceVisitor extends JavaResourceVisitor
   {
      private final List<JavaResource> classes;

      private JavaClassSourceVisitor(List<JavaResource> classes)
      {
         this.classes = classes;
      }

      @Override
      public void visit(VisitContext context, JavaResource resource)
      {
         try
         {
            JavaSource<?> javaType = resource.getJavaType();
            if (javaType.isClass())
            {
               classes.add(resource);
            }
         }
         catch (FileNotFoundException e)
         {
            // ignore
         }
      }
   }

   private static class JavaInterfaceSourceVisitor extends JavaResourceVisitor
   {
      private final List<JavaResource> classes;

      private JavaInterfaceSourceVisitor(List<JavaResource> classes)
      {
         this.classes = classes;
      }

      @Override
      public void visit(VisitContext context, JavaResource resource)
      {
         try
         {
            JavaSource<?> javaType = resource.getJavaType();
            if (javaType.isInterface())
            {
               classes.add(resource);
            }
         }
         catch (FileNotFoundException e)
         {
            // ignore
         }
      }
   }

   private static class JavaAnnotationsSourceVisitor extends JavaResourceVisitor
   {
      private final List<JavaResource> classes;

      private JavaAnnotationsSourceVisitor(List<JavaResource> classes)
      {
         this.classes = classes;
      }

      @Override
      public void visit(VisitContext context, JavaResource resource)
      {
         try
         {
            JavaSource<?> javaType = resource.getJavaType();
            if (javaType.isAnnotation())
            {
               classes.add(resource);
            }
         }
         catch (FileNotFoundException e)
         {
            // ignore
         }
      }
   }
}
