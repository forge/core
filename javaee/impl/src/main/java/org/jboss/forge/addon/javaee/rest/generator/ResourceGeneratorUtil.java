/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.rest.generator;

import java.util.Iterator;
import java.util.List;

import org.jboss.forge.addon.javaee.jpa.JPAEntityUtil;
import org.jboss.forge.addon.javaee.rest.generation.RestGenerationContext;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.projects.Project;

/**
 * A utlity class that provides information about the project or the JPA entity. This is to be used in the JAX-RS
 * Resource generators.
 *
 */
public class ResourceGeneratorUtil
{
   public static String getResourcePath(RestGenerationContext context)
   {
      String packageName = context.getTargetPackageName();
      String entityTable = JPAEntityUtil.getEntityTable(context.getEntity());
      Project project = context.getProject();
      String proposedQualifiedClassName = packageName + "." + entityTable + "Endpoint";
      String proposedResourcePath = "/" + context.getInflector().pluralize(entityTable.toLowerCase());
      RestResourceTypeVisitor resourceTypeVisitor = new RestResourceTypeVisitor();
      resourceTypeVisitor.setFound(false);
      resourceTypeVisitor.setProposedPath(proposedResourcePath);
      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      while (true)
      {
         facet.visitJavaSources(resourceTypeVisitor);
         if (resourceTypeVisitor.isFound())
         {
            if (proposedQualifiedClassName.equals(resourceTypeVisitor.getQualifiedClassNameForMatch()))
            {
               // The class might be overwritten later, so break out
               break;
            }
            proposedResourcePath = proposedResourcePath.startsWith("/") ? "forge" + proposedResourcePath : "forge/"
                     + proposedResourcePath;
            resourceTypeVisitor.setProposedPath(proposedResourcePath);
            resourceTypeVisitor.setFound(false);
         }
         else
         {
            break;
         }
      }
      return proposedResourcePath;
   }

   public static String getContentType(List<String> contentType)
   {
      StringBuilder contentTypeBuilder = new StringBuilder();
      if (contentType.size() > 1)
      {
         contentTypeBuilder.append("{");
         for (Iterator<String> iter = contentType.iterator(); iter.hasNext();)
         {
            contentTypeBuilder.append('"').append(iter.next()).append('"');
            if (iter.hasNext())
            {
               contentTypeBuilder.append(',');
            }
         }
         contentTypeBuilder.append("}");
      }
      else
      {
         String contentTypeValue = contentType.get(0);
         contentTypeBuilder.append('"').append(contentTypeValue).append('"');
      }
      return contentTypeBuilder.toString();
   }
}
