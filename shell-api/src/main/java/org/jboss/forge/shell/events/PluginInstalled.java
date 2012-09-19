/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.shell.events;

import org.jboss.forge.shell.PluginEntry;

/**
 * Fired when a plugin is installed
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public final class PluginInstalled
{
   private final PluginEntry pluginEntry;

   public PluginInstalled(PluginEntry pluginEntry)
   {
      this.pluginEntry = pluginEntry;
   }

   public PluginEntry getPluginEntry()
   {
      return pluginEntry;
   }
}