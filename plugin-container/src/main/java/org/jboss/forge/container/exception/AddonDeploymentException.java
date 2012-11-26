/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.container.exception;

public class AddonDeploymentException extends ContainerException
{

   private static final long serialVersionUID = 1L;

   public AddonDeploymentException()
   {
   }

   public AddonDeploymentException(String message, Throwable cause)
   {
      super(message, cause);
   }

   public AddonDeploymentException(String message)
   {
      super(message);
   }

   public AddonDeploymentException(Throwable cause)
   {
      super(cause);
   }

}
