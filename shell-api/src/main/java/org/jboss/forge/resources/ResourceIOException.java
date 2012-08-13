/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.resources;

/**
 * @author Mike Brock <cbrock@redhat.com>
 */
public class ResourceIOException extends RuntimeException
{
   private static final long serialVersionUID = -6669530557926742097L;

   public ResourceIOException()
   {
   }

   public ResourceIOException(final String message)
   {
      super(message);
   }

   public ResourceIOException(final String message, final Throwable cause)
   {
      super(message, cause);
   }

   public ResourceIOException(final Throwable cause)
   {
      super(cause);
   }
}
