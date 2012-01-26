/*
 * JBoss, by Red Hat.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
 */
public class Bootstrap
{
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
      init();
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
               try {
                  WeldContainer container = weld.initialize();
                  manager = container.getBeanManager();
                  weld.shutdown();
               }
               catch (Exception e) {}

               try {
                  // TODO verify plugin API versions. only activate compatible plugins.
                  loadPlugins();
                  WeldContainer container = weld.initialize();
                  manager = container.getBeanManager();
               }
               catch (Throwable e) {
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

         List<PluginEntry> installed = InstalledPluginRegistry.listByVersion(Bootstrap.class.getPackage()
                  .getImplementationVersion());

         toLoad.addAll(installed);

         // Add in the SNAPSHOT versions, we can't ignore them.
         List<PluginEntry> incompatible = InstalledPluginRegistry.list();
         incompatible.removeAll(installed);
         if (!incompatible.isEmpty())
         {
            for (PluginEntry pluginEntry : incompatible) {
               if (pluginEntry.getApiVersion().contains("SNAPSHOT"))
               {
                  toLoad.add(pluginEntry);
                  incompatible.remove(pluginEntry);
               }
            }
         }

         for (PluginEntry pluginEntry : incompatible) {
            System.out.println("Not loading plugin [" + pluginEntry.getName()
                     + "] because it references Forge API version [" + pluginEntry.getApiVersion()
                     + "] which may not be compatible with my current version [" + Bootstrap.class.getPackage()
                              .getImplementationVersion() + "]. To remove this plugin, type 'forge remove-plugin "
                     + pluginEntry + ".");
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
