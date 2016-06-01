/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.parser.java.resources;

import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.visit.ResourceVisitor;
import org.jboss.forge.addon.resource.visit.VisitContext;

/**
 * @author Rudy De Busscher Called for each Java file which is found in the project.
 */
public abstract class JavaResourceVisitor implements ResourceVisitor
{
   /**
    * Called when a Java File is found.
    * 
    * @param javaResource The JavaResource for the found file.
    */
   public abstract void visit(VisitContext context, final JavaResource javaResource);

   @Override
   public void visit(VisitContext context, Resource<?> resource)
   {
      if (resource instanceof JavaResource)
         visit(context, (JavaResource) resource);
   }
}
