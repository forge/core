/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.project;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class ProjectModelException extends RuntimeException
{
   private static final long serialVersionUID = 9036061452674068890L;

   public ProjectModelException()
   {
   }

   public ProjectModelException(String message)
   {
      super(message);
   }

   public ProjectModelException(Throwable e)
   {
      super(e);
   }

   public ProjectModelException(String message, Throwable e)
   {
      super(message, e);
   }

}
