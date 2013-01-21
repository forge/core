/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.shell.exceptions;

import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.plugins.Plugin;

/**
 * Thrown when one of a running {@link Plugin} or a command running with {@link Shell#execute(String)} are aborted.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class AbortedException extends RuntimeException
{
   private static final long serialVersionUID = -1320216827975900122L;

   public AbortedException()
   {
      super();
   }

   public AbortedException(String message, Throwable cause)
   {
      super(message, cause);
   }

   public AbortedException(String message)
   {
      super(message);
   }

   public AbortedException(Throwable cause)
   {
      super(cause);
   }
}
