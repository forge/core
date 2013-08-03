/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.javaee.rest;

import java.io.FileNotFoundException;

import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.resources.java.JavaResourceVisitor;

class RestResourceTypeVisitor implements JavaResourceVisitor
{
   private String proposedPath;
   private boolean found;
   private JavaSource<?> javaSource;

   public void setProposedPath(String proposedPath)
   {
      this.proposedPath = proposedPath;
   }

   public String getQualifiedClassNameForMatch()
   {
      if(javaSource != null)
      {
         return javaSource.getQualifiedName();
      }
      return null;
   }

   public boolean isFound()
   {
      return found;
   }

   public void setFound(boolean found)
   {
      this.found = found;
   }

   @Override
   public void visit(JavaResource javaResource)
   {
      if (!found)
      {
         try
         {
            if (javaResource.getJavaSource().getAnnotation("javax.ws.rs.Path") != null)
            {
               String path = javaResource.getJavaSource().getAnnotation("javax.ws.rs.Path")
                        .getStringValue();
               String absolutePath = path.endsWith("/") ? path.substring(0,path.lastIndexOf('/')) : path;
               if (absolutePath.equals(proposedPath))
               {
                  javaSource = javaResource.getJavaSource();
                  found = true;
               }
            }
         }
         catch (FileNotFoundException e)
         {
            throw new RuntimeException(e);
         }
      }
   }
}