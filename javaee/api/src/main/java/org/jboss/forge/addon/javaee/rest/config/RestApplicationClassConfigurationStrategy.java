/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.rest.config;

import java.io.FileNotFoundException;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.furnace.util.Assert;
import org.jboss.forge.parser.java.JavaClass;

/**
 * Configures the Rest facet through the web.xml
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class RestApplicationClassConfigurationStrategy implements RestConfigurationStrategy
{
   private final String path;
   private final JavaClass applicationClass;

   public RestApplicationClassConfigurationStrategy(JavaClass javaClass)
   {
      Assert.notNull(javaClass, "JavaClass cannot be null");
      Assert.isTrue(javaClass.hasAnnotation(ApplicationPath.class),
               "@ApplicationPath should be present in the JavaClass");
      this.applicationClass = javaClass;
      this.path = javaClass.getAnnotation(ApplicationPath.class).getStringValue();
   }

   public RestApplicationClassConfigurationStrategy(String path, JavaClass javaClass)
   {
      Assert.notNull(path, "Path cannot be null");
      Assert.notNull(javaClass, "JavaClass cannot be null");
      this.applicationClass = javaClass;
      this.path = path;
   }

   @Override
   public String getDescription()
   {
      return "Configure an Application class";
   }

   @Override
   public String getApplicationPath()
   {
      return path;
   }

   public JavaClass getResource()
   {
      return applicationClass;
   }

   @Override
   public void install(Project project)
   {
      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      applicationClass.setSuperType(Application.class);
      if (applicationClass.hasAnnotation(ApplicationPath.class))
      {
         applicationClass.getAnnotation(ApplicationPath.class).setStringValue(path);
      }
      else
      {
         applicationClass.addAnnotation(ApplicationPath.class).setStringValue(path);
      }
      try
      {
         facet.saveJavaSource(applicationClass);
      }
      catch (FileNotFoundException ffe)
      {
         throw new IllegalStateException(ffe);
      }
   }

   @Override
   public void uninstall(Project project)
   {
      // TODO Auto-generated method stub

   }
}