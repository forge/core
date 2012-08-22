/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.shell.plugins.builtin;

import java.util.Set;
import java.util.TreeSet;

import javax.enterprise.event.Observes;

import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.ShellColor;
import org.jboss.forge.shell.command.PluginRegistry;
import org.jboss.forge.shell.events.PostStartup;
import org.jboss.forge.shell.events.ReinitializeEnvironment;

/**
 * Display a diff of available commands when starting forge after installing/removing a plugin
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public class PluginDiffObserver
{

   private static volatile Set<String> LAST_PLUGINS_DIFF;

   /**
    * Called when Forge is restarted
    */
   public void onRestart(@Observes ReinitializeEnvironment restart, PluginRegistry pluginRegistry)
   {
      LAST_PLUGINS_DIFF = new TreeSet<String>();
      LAST_PLUGINS_DIFF.addAll(pluginRegistry.getPlugins().keySet());
   }

   /**
    * Called when Forge is booted up
    */
   public void onInitialize(@Observes PostStartup start, PluginRegistry pluginRegistry, Shell shell)
   {
      if (LAST_PLUGINS_DIFF != null)
      {
         Set<String> loadedPlugins = pluginRegistry.getPlugins().keySet();
         Set<String> currentPlugins = new TreeSet<String>();
         currentPlugins.addAll(loadedPlugins);
         currentPlugins.removeAll(LAST_PLUGINS_DIFF);
         if (!LAST_PLUGINS_DIFF.isEmpty() && !currentPlugins.isEmpty())
         {
            shell.print("The following plugins have been activated: ");
            shell.println(ShellColor.BOLD, currentPlugins.toString());
         }
         LAST_PLUGINS_DIFF.removeAll(loadedPlugins);
         if (!LAST_PLUGINS_DIFF.isEmpty())
         {
            shell.print("The following plugins have been deactivated: ");
            shell.println(ShellColor.BOLD, LAST_PLUGINS_DIFF.toString());
         }
         LAST_PLUGINS_DIFF = null;
      }

   }

}
