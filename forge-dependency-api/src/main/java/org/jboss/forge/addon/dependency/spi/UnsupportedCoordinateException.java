/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.dependency.spi;

public class UnsupportedCoordinateException extends RuntimeException
{

   private static final long serialVersionUID = 1L;

   public UnsupportedCoordinateException()
   {
      super();
   }

   public UnsupportedCoordinateException(String message, Throwable cause)
   {
      super(message, cause);
   }

   public UnsupportedCoordinateException(String message)
   {
      super(message);
   }

   public UnsupportedCoordinateException(Throwable cause)
   {
      super(cause);
   }

}
