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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.forge.container.AddonRegistry.AddonEntry;
import org.jboss.forge.container.exception.ContainerException;
import org.jboss.forge.container.meta.PluginMetadata;
import org.jboss.forge.container.util.ThreadNamingRunnable;
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

      AddonModuleRegistry registry = new AddonModuleRegistry();

      try
      {
         Set<Module> addons = loadAddons();

         // Make sure Weld uses ThreadSafe singletons.
         SingletonProvider.initialize(new TCCLSingletonProvider());

         ModuleLoader moduleLoader = Module.getBootModuleLoader();
         Module forge = moduleLoader.loadModule(ModuleIdentifier.fromString("org.jboss.forge:main"));

         ExecutorService threadPool = Executors.newCachedThreadPool();

         ControlRunnable controlRunnable = new ControlRunnable(forge, registry, addons);
         threadPool.submit(new ThreadNamingRunnable(forge.getIdentifier().getName() + ":"
                  + forge.getIdentifier().getSlot(), controlRunnable));

         for (Module module : addons)
         {
            AddonRunnable pluginRunnable = new AddonRunnable(module, registry, addons);
            threadPool.submit(new ThreadNamingRunnable(module.getIdentifier().getName()
                     + ":" + module.getIdentifier().getSlot(), pluginRunnable));
         }

         // Disable new tasks from being submitted
         threadPool.shutdown();
         // Wait for awhile for existing tasks to terminate
         threadPool.awaitTermination(10, TimeUnit.MINUTES);

         Map<Module, Map<String, List<PluginMetadata>>> loadedAddons = registry.getPlugins();
         for (Entry<Module, Map<String, List<PluginMetadata>>> entry : loadedAddons.entrySet())
         {
            System.out.println("Plugins from addon module [" + entry.getKey().getIdentifier() + "] - "
                     + entry.getValue());
         }
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

      List<AddonEntry> toLoad = new ArrayList<AddonRegistry.AddonEntry>();
      List<AddonEntry> installed = AddonRegistry.listByAPICompatibleVersion(AddonRegistry
               .getRuntimeAPIVersion());

      toLoad.addAll(installed);

      List<AddonEntry> incompatible = AddonRegistry.list();
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
