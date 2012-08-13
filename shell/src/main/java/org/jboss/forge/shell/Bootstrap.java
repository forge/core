/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.jboss.forge.shell.InstalledPluginRegistry.PluginEntry;
import org.jboss.forge.shell.events.AcceptUserInput;
import org.jboss.forge.shell.events.PostStartup;
import org.jboss.forge.shell.events.PreStartup;
import org.jboss.forge.shell.events.ReinitializeEnvironment;
import org.jboss.forge.shell.events.Shutdown;
import org.jboss.forge.shell.events.Startup;
import org.jboss.modules.Module;
import org.jboss.modules.ModuleIdentifier;
import org.jboss.modules.ModuleLoader;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author Mike Brock
 * @author Ronald van Kuijk
 * @author <a href="mailto:koen.aers@gmail.com">Koen Aers</a>
 */
public class Bootstrap
{

   public static final String PROP_PLUGIN_DIR = "org.jboss.forge.pluginDir";
   public static final String PROP_EVALUATE = "org.jboss.forge.evaluate";
   private static final String ARG_PLUGIN_DIR = "-pluginDir";
   private static final String ARG_EVALUATE = "-e";

   private static boolean pluginSystemEnabled = !Boolean.getBoolean("forge.plugins.disable");
   private static Thread currentShell = null;
   private static boolean restartRequested = false;
   private static File workingDir = new File("").getAbsoluteFile();
   private static ClassLoader mainClassLoader;

   @Inject
   private BeanManager manager;

   public static void main(final String[] args)
   {
      mainClassLoader = Thread.currentThread().getContextClassLoader();
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
      do
      {
         currentShell = new Thread(new Runnable()
         {
            @Override
            public void run()
            {
               initLogging();

               boolean restarting = restartRequested;
               restartRequested = false;

               Weld weld = new ModularWeld();
               BeanManager manager = null;

               // FIXME this plugin loading scheme causes classloading issues w/weld because weld cannot load classes
               // from its own classloaders before plugins are loaded and pollute the classpath.
               // We can work around it by loading weld before we load plugins, then restarting weld, but this is SLOW.
               try
               {
                  WeldContainer container = weld.initialize();
                  manager = container.getBeanManager();
                  weld.shutdown();
               }
               catch (Exception e)
               {
               }

               try
               {
                  // TODO verify plugin API versions. only activate compatible plugins.
                  loadPlugins();
                  WeldContainer container = weld.initialize();
                  manager = container.getBeanManager();
               }
               catch (Throwable e)
               {
                  // Boot up with external plugins disabled.
                  System.out
                           .println("Plugin system disabled due to failure while loading one or more plugins; try removing offending plugins with \"forge remove-plugin <TAB>\".");
                  e.printStackTrace();

                  Thread.currentThread().setContextClassLoader(mainClassLoader);

                  initLogging();
                  WeldContainer container = weld.initialize();
                  manager = container.getBeanManager();
               }

               manager.fireEvent(new PreStartup());
               manager.fireEvent(new Startup(workingDir, restarting));
               manager.fireEvent(new PostStartup());
               manager.fireEvent(new AcceptUserInput());
               weld.shutdown();
            }
         });

         currentShell.start();
         try
         {
            currentShell.join();
         }
         catch (InterruptedException e)
         {
            throw new RuntimeException(e);
         }
      }
      while (restartRequested);
   }

   public void observeReinitialize(@Observes final ReinitializeEnvironment event, final Shell shell)
   {
      workingDir = shell.getCurrentDirectory().getUnderlyingResourceObject();
      manager.fireEvent(new Shutdown());
      restartRequested = true;
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

   synchronized private static void loadPlugins()
   {

      if (!pluginSystemEnabled)
         return;

      try
      {
         ModuleLoader moduleLoader = Module.getBootModuleLoader();

         CompositeClassLoader composite = new CompositeClassLoader();
         composite.add(Module.forClassLoader(Bootstrap.class.getClassLoader(), true).getClassLoader());

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
               composite.add(module.getClassLoader());
            }
            catch (Exception e)
            {
               System.out.println("Failed loading: " + plugin);
               e.printStackTrace();
            }
         }

         Module forge = moduleLoader.loadModule(ModuleIdentifier.fromString("org.jboss.forge:main"));

         composite.add(forge.getClassLoader());
         Thread.currentThread().setContextClassLoader(composite);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }
}
