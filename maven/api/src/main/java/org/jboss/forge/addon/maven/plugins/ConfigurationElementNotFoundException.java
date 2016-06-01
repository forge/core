/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.plugins;

public class ConfigurationElementNotFoundException extends RuntimeException
{
   private static final long serialVersionUID = -443270510723493609L;

   public ConfigurationElementNotFoundException()
   {
      super("No message");
   }

   public ConfigurationElementNotFoundException(final String elementName)
   {
      super("Configuration element with name '" + elementName + "' doesn't exist");
   }

   public ConfigurationElementNotFoundException(String message, Throwable cause)
   {
      super(message, cause);
   }
}
