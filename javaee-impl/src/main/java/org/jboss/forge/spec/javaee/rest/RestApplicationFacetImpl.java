/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.javaee.rest;

import java.io.FileNotFoundException;

import javax.inject.Inject;

import org.jboss.forge.env.Configuration;
import org.jboss.forge.env.ConfigurationFactory;
import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.resources.java.JavaResourceVisitor;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.spec.javaee.RestApplicationFacet;
import org.jboss.forge.spec.javaee.RestFacet;

/**
 * @Author Paul Bakker - paul.bakker@luminis.eu
 */
@Alias("forge.spec.jaxrs.applicationclass")
@RequiresFacet(JavaSourceFacet.class)
public class RestApplicationFacetImpl extends BaseFacet implements RestApplicationFacet
{
   private String classPackage;
   private String className;
   private String rootPath;

   @Inject
   private ConfigurationFactory configurationFactory;
   
   // Do not refer this field directly. Use the getProjectConfiguration() method instead.
   private Configuration configuration;

   @Override
   public boolean install()
   {
      if (!isInstalled())
      {
         JavaSourceFacet javaSourceFacet = project.getFacet(JavaSourceFacet.class);

         JavaClass applicationClass = JavaParser.create(JavaClass.class)
                  .setPackage(classPackage)
                  .setName(className)
                  .setSuperType("javax.ws.rs.core.Application")
                  .addAnnotation("javax.ws.rs.ApplicationPath").setStringValue(rootPath).getOrigin();

         applicationClass.addImport("javax.ws.rs.core.Application");
         applicationClass.addImport("javax.ws.rs.ApplicationPath");

         try
         {
            javaSourceFacet.saveJavaSource(applicationClass);
         }
         catch (FileNotFoundException e)
         {
            throw new RuntimeException(e);
         }
      }

      return true;
   }

   @Override
   public boolean isInstalled()
   {
      JavaSourceFacet javaSourceFacet = project.getFacet(JavaSourceFacet.class);

      Configuration projectConfiguration = getProjectConfiguration();
      classPackage = projectConfiguration.getString(REST_APPLICATIONCLASS_PACKAGE);
      className = projectConfiguration.getString(REST_APPLICATIONCLASS_NAME);
      rootPath = projectConfiguration.getString(RestFacet.ROOTPATH);
      
      if ((classPackage == null || className == null) && !findApplicationClass())
      {
         return false;
      }

      try
      {
         JavaResource javaResource = javaSourceFacet.getJavaResource(classPackage + "." + className);
         if (javaResource.exists() || findApplicationClass())
         {
            return true;
         }

      }
      catch (FileNotFoundException e)
      {
         return false;
      }

      return false;
   }

   private boolean findApplicationClass()
   {
      JavaSourceFacet javaSourceFacet = project.getFacet(JavaSourceFacet.class);

      Configuration projectConfiguration = getProjectConfiguration();
      projectConfiguration.clearProperty(REST_APPLICATIONCLASS_NAME);
      projectConfiguration.clearProperty(REST_APPLICATIONCLASS_PACKAGE);

      javaSourceFacet.visitJavaSources(new JavaResourceVisitor()
      {
         Configuration configuration = getProjectConfiguration();
         boolean found = false;

         @Override
         public void visit(JavaResource javaResource)
         {
            if (!found)
            {
               try
               {
                  if (javaResource.getJavaSource().getAnnotation("javax.ws.rs.ApplicationPath") != null)
                  {
                     configuration
                              .setProperty(REST_APPLICATIONCLASS_PACKAGE, javaResource.getJavaSource().getPackage());
                     configuration.setProperty(REST_APPLICATIONCLASS_NAME, javaResource.getJavaSource().getName());
                     configuration.setProperty(RestFacet.ROOTPATH,
                              javaResource.getJavaSource().getAnnotation("javax.ws.rs.ApplicationPath")
                                       .getStringValue());
                     found = true;
                  }
               }
               catch (FileNotFoundException e)
               {
                  throw new RuntimeException(e);
               }
            }
         }
      });

      return projectConfiguration.getString(REST_APPLICATIONCLASS_NAME) != null;
   }

   @Override
   public void setApplicationPath(String path)
   {
      getProjectConfiguration().setProperty(RestFacet.ROOTPATH, path);

      if (classPackage == null || className == null)
      {
         reportConfigurationError(className);
      }

      JavaSourceFacet javaSourceFacet = project.getFacet(JavaSourceFacet.class);

      try
      {
         String classname = classPackage + "." + this.className;
         JavaResource javaResource = javaSourceFacet.getJavaResource(classname);
         if (!javaResource.exists())
         {
            reportConfigurationError(classname);
         }

         javaResource.getJavaSource().getAnnotation("javax.ws.rs.ApplicationPath").setStringValue(path);

      }
      catch (FileNotFoundException e)
      {
         reportConfigurationError(className);
      }

   }

   private void reportConfigurationError(String classname)
   {
      throw new RuntimeException("Error setting application path. The class '" + classname
               + "' in your configuration file does not exist. Run rest setup again.");
   }
   
   /**
    * Important: Use this method always to obtain the configuration. Do not invoke this inside a constructor since the
    * returned {@link Configuration} instance would not be the project scoped one.
    * 
    * @return The project scoped {@link Configuration} instance
    */
   private Configuration getProjectConfiguration()
   {
      if (this.configuration == null)
      {
         this.configuration = configurationFactory.getProjectConfig(project);
      }
      return this.configuration;
   }
}
