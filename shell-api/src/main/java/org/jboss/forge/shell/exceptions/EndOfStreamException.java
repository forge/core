/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.shell.exceptions;

import java.io.InputStream;

import org.jboss.forge.shell.plugins.Plugin;

/**
 * Used to signal when the end of an {@link InputStream} has been reached. This exception should *NEVER* be caught by a
 * {@link Plugin} or other extension.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class EndOfStreamException extends RuntimeException
{
   private static final long serialVersionUID = 1814562507419409858L;

   public EndOfStreamException()
   {
      super();
   }

   public EndOfStreamException(final String message, final Throwable e)
   {
      super(message, e);
   }

   public EndOfStreamException(final String message)
   {
      super(message);
   }

   public EndOfStreamException(final Throwable e)
   {
      super(e);
   }

}
