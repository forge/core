package org.jboss.forge.addon.parser.java.beans;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.parser.java.resources.JavaResourceVisitor;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.resource.visit.VisitContext;
import org.jboss.forge.roaster.model.source.JavaSource;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ProjectOperations
{
   public List<JavaResource> getProjectClasses(Project project)
   {
      final List<JavaResource> classes = new ArrayList<>();
      if (project != null)
      {
         project.getFacet(JavaSourceFacet.class).visitJavaSources(new JavaClassSourceVisitor(classes));
      }
      return classes;
   }

   public List<JavaResource> getProjectAnnotations(Project project)
   {
      final List<JavaResource> classes = new ArrayList<>();
      if (project != null)
      {
         project.getFacet(JavaSourceFacet.class).visitJavaSources(new JavaAnnotationsSourceVisitor(classes));
      }
      return classes;
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
