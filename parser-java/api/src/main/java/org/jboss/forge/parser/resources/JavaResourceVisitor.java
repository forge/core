/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.parser.resources;

/**
 * @author Rudy De Busscher Called for each Java file which is found in the project.
 */
public interface JavaResourceVisitor
{
   /**
    * Called when a Java File is found.
    * 
    * @param javaResource The JavaResource for the found file.
    */
   void visit(final JavaResource javaResource);
}
