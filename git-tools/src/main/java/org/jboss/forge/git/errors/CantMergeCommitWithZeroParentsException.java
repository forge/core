/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.git.errors;

/**
 * @author <a href="mailto:jevgeni.zelenkov@gmail.com">Jevgeni Zelenkov</a>
 * 
 */
public class CantMergeCommitWithZeroParentsException extends Exception
{
   private static final long serialVersionUID = 1L;

   public CantMergeCommitWithZeroParentsException(String message, Throwable cause)
   {
      super(message, cause);
   }

   public CantMergeCommitWithZeroParentsException(String message)
   {
      super(message);
   }
}
