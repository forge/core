/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.rest.generator;

import java.io.FileNotFoundException;

import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.parser.java.resources.JavaResourceVisitor;
import org.jboss.forge.addon.resource.visit.VisitContext;
import org.jboss.forge.roaster.model.source.JavaSource;

class RestResourceTypeVisitor extends JavaResourceVisitor
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
      if (javaSource != null)
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
   public void visit(VisitContext context, JavaResource javaResource)
   {
      if (!found)
      {
         try
         {
            JavaSource<?> javaType = javaResource.getJavaType();
            if (javaType.getAnnotation("javax.ws.rs.Path") != null)
            {
               String path = javaType.getAnnotation("javax.ws.rs.Path")
                        .getStringValue();
               String absolutePath = path.endsWith("/") ? path.substring(0, path.lastIndexOf('/')) : path;
               if (absolutePath.equals(proposedPath))
               {
                  javaSource = javaType;
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