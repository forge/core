/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.resources;

import org.jboss.forge.project.ProjectModelException;

/**
 * Represents a generic Exception thrown by the Forge {@link Resource} API
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class ResourceException extends ProjectModelException
{
   private static final long serialVersionUID = 1532458466162580423L;

   public ResourceException()
   {
      super();
   }

   public ResourceException(String message, Throwable e)
   {
      super(message, e);
   }

   public ResourceException(String message)
   {
      super(message);
   }

   public ResourceException(Throwable e)
   {
      super(e);
   }

}
