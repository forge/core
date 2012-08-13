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
public class UnsatisfiedPackagingTypeException extends ConstraintException
{
   private static final long serialVersionUID = 1041156085212467520L;

   public UnsatisfiedPackagingTypeException()
   {
      super();
   }

   public UnsatisfiedPackagingTypeException(final String message, final Throwable cause)
   {
      super(message, cause);
   }

   public UnsatisfiedPackagingTypeException(final String message)
   {
      super(message);
   }

   public UnsatisfiedPackagingTypeException(final Throwable cause)
   {
      super(cause);
   }

}
