/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.env;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class ConfigurationException extends RuntimeException
{
   private static final long serialVersionUID = -6527201091945715721L;

   public ConfigurationException()
   {
   }

   public ConfigurationException(final String message)
   {
      super(message);
   }

   public ConfigurationException(final Throwable e)
   {
      super(e);
   }

   public ConfigurationException(final String message, final Throwable e)
   {
      super(message, e);
   }

}
