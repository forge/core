/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.exceptions;

import org.jboss.forge.shell.command.PluginMetadata;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class PluginExecutionException extends ShellExecutionException
{
   private static final long serialVersionUID = -6474891123733228235L;
   private final PluginMetadata plugin;

   public PluginExecutionException(final PluginMetadata plugin, final String message)
   {
      super(message);
      this.plugin = plugin;
   }

   public PluginExecutionException(final PluginMetadata plugin, final Throwable e)
   {
      super(e);
      this.plugin = plugin;
   }

   public PluginExecutionException(final PluginMetadata plugin, final String message, final Throwable e)
   {
      super(message, e);
      this.plugin = plugin;
   }

   public PluginMetadata getPlugin()
   {
      return plugin;
   }

}
