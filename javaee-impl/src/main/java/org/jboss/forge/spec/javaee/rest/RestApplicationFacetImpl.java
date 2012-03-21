/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.forge.spec.javaee.rest;

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

import javax.inject.Inject;
import java.io.FileNotFoundException;

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
