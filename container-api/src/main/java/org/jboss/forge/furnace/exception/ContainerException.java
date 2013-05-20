/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.furnace.exception;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class ContainerException extends RuntimeException
{
   private static final long serialVersionUID = 5266075954460779189L;

   public ContainerException()
   {
      super();
   }

   public ContainerException(String message, Throwable cause)
   {
      super(message, cause);
   }

   public ContainerException(String message)
   {
      super(message);
   }

   public ContainerException(Throwable cause)
   {
      super(cause);
   }

}
