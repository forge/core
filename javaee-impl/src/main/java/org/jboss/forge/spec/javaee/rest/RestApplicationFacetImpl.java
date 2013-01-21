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
@RequiresFacet({ RestFacet.class, JavaSourceFacet.class })
public class RestApplicationFacetImpl extends BaseFacet implements RestApplicationFacet
{
   private String classPackage;
   private String className;
   private String rootPath;

   @Inject
   private Configuration configuration;

   @Inject
   public RestApplicationFacetImpl(Configuration configuration)
   {
      classPackage = configuration.getString(REST_APPLICATIONCLASS_PACKAGE);
      className = configuration.getString(REST_APPLICATIONCLASS_NAME);
      rootPath = configuration.getString(RestFacet.ROOTPATH);
   }

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

      configuration.clearProperty(REST_APPLICATIONCLASS_NAME);
      configuration.clearProperty(REST_APPLICATIONCLASS_PACKAGE);

      javaSourceFacet.visitJavaSources(new JavaResourceVisitor()
      {
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
                     configuration.setProperty(REST_APPLICATIONCLASS_NAME, javaResource.getFullyQualifiedName());
                     configuration.setProperty(RestFacet.ROOTPATH,
                              javaResource.getJavaSource().getAnnotation("javax.ws.rs.ApplicationPath")
                                       .getLiteralValue());
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

      return configuration.getString(REST_APPLICATIONCLASS_NAME) != null;
   }

   @Override
   public void setApplicationPath(String path)
   {
      configuration.setProperty(RestFacet.ROOTPATH, path);

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
}
