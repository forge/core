/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.exceptions;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class ShellExecutionException extends ShellException
{
   private static final long serialVersionUID = 8113296404786359776L;

   public ShellExecutionException()
   {
      super();
   }

   public ShellExecutionException(final String message, final Throwable cause)
   {
      super(message, cause);
   }

   public ShellExecutionException(final String message)
   {
      super(message);
   }

   public ShellExecutionException(final Throwable cause)
   {
      super(cause);
   }
}
