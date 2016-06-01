/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.dependencies;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class DependencyException extends RuntimeException
{
   private static final long serialVersionUID = 3858719629364658345L;

   public DependencyException()
   {
      super("No message");
   }

   public DependencyException(String message)
   {
      super(message);
   }

   public DependencyException(String message, Throwable e)
   {
      super(message, e);
   }

}
