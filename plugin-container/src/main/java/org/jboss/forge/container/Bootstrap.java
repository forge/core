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
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.forge.container.AddonUtil.AddonEntry;
import org.jboss.forge.container.exception.ContainerException;
import org.jboss.forge.container.modules.AddonModuleLoader;
import org.jboss.forge.container.services.ServiceRegistry;
import org.jboss.modules.Module;
import org.jboss.modules.ModuleIdentifier;
import org.jboss.modules.ModuleLoadException;
import org.jboss.modules.ModuleLoader;
import org.jboss.weld.bootstrap.api.SingletonProvider;
import org.jboss.weld.bootstrap.api.helpers.TCCLSingletonProvider;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class Bootstrap
{
   public static final String PROP_PLUGIN_DIR = "org.jboss.forge.pluginDir";
   public static final String PROP_EVALUATE = "org.jboss.forge.evaluate";
   public static final String PROP_CONCURRENT_PLUGINS = "org.jboss.forge.concurrent.Plugins";
   private static final String ARG_PLUGIN_DIR = "-pluginDir";
   private static final String ARG_EVALUATE = "-e";

   private static final int BATCH_SIZE = Integer.getInteger(PROP_CONCURRENT_PLUGINS, 4);

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

      try
      {
         Set<Module> addons = loadAddons();
         int batchSize = Math.min(BATCH_SIZE, addons.size());
         System.out.println("Batch size = " + batchSize);

         // Make sure Weld uses ThreadSafe singletons.
         SingletonProvider.initialize(new TCCLSingletonProvider());

         ModuleLoader moduleLoader = Module.getBootModuleLoader();
         Module forge = moduleLoader.loadModule(ModuleIdentifier.fromString("org.jboss.forge:main"));
         AddonRegistry registry = AddonRegistry.registry;
         ControlRunnable controlRunnable = new ControlRunnable(forge, registry, addons);
         Thread controlThread = new Thread(controlRunnable, forge.getIdentifier().getName() + ":"
                  + forge.getIdentifier().getSlot());
         controlThread.start();

         Set<Thread> threads = new HashSet<Thread>();

         int started = 0;
         for (Module module : addons)
         {
            while (registry.getServices().size() + batchSize <= started)
            {
               Thread.sleep(10);
            }

            AddonRunnable pluginRunnable = new AddonRunnable(module, registry, addons);
            Thread pluginThread = new Thread(pluginRunnable, module.getIdentifier().getName()
                     + ":" + module.getIdentifier().getSlot());
            threads.add(pluginThread);
            pluginThread.start();

            started++;

         }

         boolean alive;
         do
         {
            Thread.sleep(10);
            alive = false;
            for (Thread thread : threads)
            {
               if (thread.isAlive())
               {
                  alive = true;
               }
            }
         }
         while (alive == true);

         Map<Module, ServiceRegistry> loadedAddons = registry.getServices();
         for (Entry<Module, ServiceRegistry> entry : loadedAddons.entrySet())
         {
            System.out.println("Plugins from addon module [" + entry.getKey().getIdentifier() + "] - "
                     + entry.getValue());
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

   synchronized private static Set<Module> loadAddons()
   {
      Set<Module> result = new HashSet<Module>();

      List<AddonEntry> toLoad = new ArrayList<AddonUtil.AddonEntry>();
      List<AddonEntry> installed = AddonUtil.listByAPICompatibleVersion(AddonUtil
               .getRuntimeAPIVersion());

      toLoad.addAll(installed);

      List<AddonEntry> incompatible = AddonUtil.list();
      incompatible.removeAll(installed);

      for (AddonEntry pluginEntry : incompatible)
      {
         System.out.println("Not loading plugin [" + pluginEntry.getName()
                  + "] because it references Forge API version [" + pluginEntry.getApiVersion()
                  + "] which may not be compatible with my current version [" + Bootstrap.class.getPackage()
                           .getImplementationVersion() + "]. To remove this plugin, type 'forge remove-plugin "
                  + pluginEntry + ". Otherwise, try installing a new version of the plugin.");
      }

      ModuleLoader moduleLoader = new AddonModuleLoader(installed);
      for (AddonEntry plugin : toLoad)
      {
         try
         {
            Module module = moduleLoader.loadModule(ModuleIdentifier.fromString(plugin.toModuleId()));
            result.add(module);
         }
         catch (Exception e)
         {
            throw new ContainerException("Failed loading module for plugin [" + plugin + "]", e);
         }
      }

      return result;
   }
}
