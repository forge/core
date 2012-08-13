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
public class ShellParserException extends ShellException
{
   private static final long serialVersionUID = -6497472768450068197L;

   public ShellParserException()
   {
      super();
   }

   public ShellParserException(final String message, final Throwable cause)
   {
      super(message, cause);
   }

   public ShellParserException(final String message)
   {
      super(message);
   }

   public ShellParserException(final Throwable cause)
   {
      super(cause);
   }
}
