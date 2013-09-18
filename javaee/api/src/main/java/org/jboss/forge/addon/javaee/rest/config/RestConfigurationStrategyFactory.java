/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.rest.config;

import java.io.FileNotFoundException;

import javax.ws.rs.ApplicationPath;

import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.parser.java.resources.JavaResourceVisitor;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.JavaSource;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class RestConfigurationStrategyFactory
{
   public static RestConfigurationStrategy from(Project project)
   {
      final RestConfigurationStrategy[] configurationStrategy = new RestConfigurationStrategy[1];
      // Check if there is any class with @ApplicationPath
      if (project.hasFacet(JavaSourceFacet.class))
      {
         JavaSourceFacet javaSourceFacet = project.getFacet(JavaSourceFacet.class);
         javaSourceFacet.visitJavaSources(new JavaResourceVisitor()
         {
            @Override
            public void visit(JavaResource javaResource)
            {
               try
               {
                  JavaSource<?> javaSource = javaResource.getJavaSource();
                  if (javaSource.hasAnnotation(ApplicationPath.class))
                  {
                     configurationStrategy[0] = createUsingJavaClass((JavaClass) javaSource);
                  }
               }
               catch (FileNotFoundException e)
               {
               }
            }
         });
      }
      if (configurationStrategy[0] == null)
      {
         // Check Web.xml
         String path = RestWebXmlConfigurationStrategy.getServletPath(project);
         if (path != null)
         {
            configurationStrategy[0] = createUsingWebXml(path);
         }
      }
      return configurationStrategy[0];
   }

   public static RestConfigurationStrategy createUsingWebXml(String path)
   {
      return new RestWebXmlConfigurationStrategy(path);
   }

   public static RestConfigurationStrategy createUsingJavaClass(String path, JavaClass javaClass)
   {
      return new RestApplicationClassConfigurationStrategy(path, javaClass);
   }

   public static RestConfigurationStrategy createUsingJavaClass(JavaClass javaClass)
   {
      return new RestApplicationClassConfigurationStrategy(javaClass);
   }
}
