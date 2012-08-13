/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.project.build;

/**
 * Represents an exception during project building.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class BuildException extends RuntimeException
{
   private static final long serialVersionUID = 7523197340850216859L;

   public BuildException()
   {
      super();
   }

   public BuildException(final String message, final Throwable cause)
   {
      super(message, cause);
   }

   public BuildException(final String message)
   {
      super(message);
   }

   public BuildException(final Throwable cause)
   {
      super(cause);
   }

}
