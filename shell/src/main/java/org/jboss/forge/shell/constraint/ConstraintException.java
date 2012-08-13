/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.constraint;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class ConstraintException extends Exception
{
   private static final long serialVersionUID = 5921716189368967332L;

   public ConstraintException()
   {
      super();
   }

   public ConstraintException(final String message, final Throwable cause)
   {
      super(message, cause);
   }

   public ConstraintException(final String message)
   {
      super(message);
   }

   public ConstraintException(final Throwable cause)
   {
      super(cause);
   }

}
