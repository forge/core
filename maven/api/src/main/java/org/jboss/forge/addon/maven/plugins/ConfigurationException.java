/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.plugins;

/**
 * @author <a href="mailto:paul.bakker.nl@gmail.com">Paul Bakker</a>
 */
public class ConfigurationException extends RuntimeException
{
   private static final long serialVersionUID = -1348437184032449458L;

   public ConfigurationException(final String s, final Exception ex)
   {
      super(s, ex);
   }
}
