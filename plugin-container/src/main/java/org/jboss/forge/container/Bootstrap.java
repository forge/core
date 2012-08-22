/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.container;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.forge.container.InstalledPluginRegistry.PluginEntry;
import org.jboss.forge.container.exception.ContainerException;
import org.jboss.modules.Module;
import org.jboss.modules.ModuleIdentifier;
import org.jboss.modules.ModuleLoadException;
import org.jboss.modules.ModuleLoader;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class Bootstrap
{
   public static final String PROP_PLUGIN_DIR = "org.jboss.forge.pluginDir";
   public static final String PROP_EVALUATE = "org.jboss.forge.evaluate";
   private static final String ARG_PLUGIN_DIR = "-pluginDir";
   private static final String ARG_EVALUATE = "-e";

   public static void main(final String[] args)
   {
      readArguments(args);
      init();
   }

   private static void readArguments(String[] args)
   {
      readPluginDirArgument(args);
      readEvaluateArgument(args);
   }

   private static void readPluginDirArgument(String[] args)
   {
      for (int i = 0; i < args.length; i++)
      {
         if (ARG_PLUGIN_DIR.equals(args[i]) && i + 1 < args.length)
         {
            System.setProperty(PROP_PLUGIN_DIR, args[i + 1]);
            return;
         }
      }
   }

   private static void readEvaluateArgument(String[] args)
   {
      for (int i = 0; i < args.length; i++)
      {
         if (ARG_EVALUATE.equals(args[i]) && i + 1 < args.length)
         {
            System.setProperty(PROP_EVALUATE, args[i + 1]);
            return;
         }
      }
   }

   private static void init()
   {
      initLogging();

      PluginModuleRegistry registry = new PluginModuleRegistry();

      try
      {
         ModuleLoader moduleLoader = Module.getBootModuleLoader();
         Module forge = moduleLoader.loadModule(ModuleIdentifier.fromString("org.jboss.forge:main"));
         Thread controlThread = new Thread(new PluginRunnable(forge, registry), forge.getIdentifier().getName() + ":" + forge.getIdentifier().getSlot());
         controlThread.start();

         Set<Module> plugins = loadPlugins(moduleLoader);

         for (Module module : plugins)
         {
            Thread pluginThread = new Thread(new PluginRunnable(module, registry), module.getIdentifier().getName() + ":" + module.getIdentifier().getSlot());
            pluginThread.start();
         }

         controlThread.join();
      }
      catch (ModuleLoadException e)
      {
         throw new ContainerException(e);
      }
      catch (InterruptedException e)
      {
         throw new ContainerException(e);
      }
   }

   private static void initLogging()
   {
      String[] loggerNames = new String[] { "", "main", Logger.GLOBAL_LOGGER_NAME };
      for (String loggerName : loggerNames)
      {
         Logger globalLogger = Logger.getLogger(loggerName);
         Handler[] handlers = globalLogger.getHandlers();
         for (Handler handler : handlers)
         {
            handler.setLevel(Level.SEVERE);
            globalLogger.removeHandler(handler);
         }
      }
   }

   synchronized private static Set<Module> loadPlugins(ModuleLoader bootLoader)
   {
      Set<Module> result = new HashSet<Module>();

      try
      {
         ModuleLoader moduleLoader = new PluginModuleLoader();

         List<PluginEntry> toLoad = new ArrayList<InstalledPluginRegistry.PluginEntry>();
         List<PluginEntry> installed = InstalledPluginRegistry.listByAPICompatibleVersion(InstalledPluginRegistry
                  .getRuntimeAPIVersion());

         toLoad.addAll(installed);

         List<PluginEntry> incompatible = InstalledPluginRegistry.list();
         incompatible.removeAll(installed);

         for (PluginEntry pluginEntry : incompatible)
         {
            System.out.println("Not loading plugin [" + pluginEntry.getName()
                     + "] because it references Forge API version [" + pluginEntry.getApiVersion()
                     + "] which may not be compatible with my current version [" + Bootstrap.class.getPackage()
                              .getImplementationVersion() + "]. To remove this plugin, type 'forge remove-plugin "
                     + pluginEntry + ". Otherwise, try installing a new version of the plugin.");
         }

         for (PluginEntry plugin : toLoad)
         {
            try
            {
               Module module = moduleLoader.loadModule(ModuleIdentifier.fromString(plugin.toModuleId()));
               result.add(module);
            }
            catch (Exception e)
            {
               System.out.println("Failed loading module for plugin [" + plugin + "]");
               e.printStackTrace();
            }
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }

      return result;
   }
}
