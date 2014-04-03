/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.git.exceptions;

/**
 * @author <a href="mailto:jevgeni.zelenkov@gmail.com">Jevgeni Zelenkov</a>
 */
public class CantMergeCommitException extends Exception
{
   private static final long serialVersionUID = 1L;

   public CantMergeCommitException(String message, Throwable cause)
   {
      super(message, cause);
   }

   public CantMergeCommitException(String message)
   {
      super(message);
   }
}
