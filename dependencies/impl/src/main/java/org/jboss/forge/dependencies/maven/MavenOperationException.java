/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.dependencies.maven;

public class MavenOperationException extends RuntimeException
{
   private static final long serialVersionUID = 3436297543207124937L;

   public MavenOperationException()
   {
      super();
   }

   public MavenOperationException(String message, Throwable cause)
   {
      super(message, cause);
   }

   public MavenOperationException(String message)
   {
      super(message);
   }

   public MavenOperationException(Throwable cause)
   {
      super(cause);
   }
}
