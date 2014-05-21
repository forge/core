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

public class ProjectOperations
{
   public List<JavaResource> getProjectClasses(Project project)
   {
      final List<JavaResource> classes = new ArrayList<>();
      if (project != null)
      {
         project.getFacet(JavaSourceFacet.class).visitJavaSources(new JavaResourceVisitor()
         {

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
         });
      }
      return classes;

   }
}
