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
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

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
   private static Thread currentShell = null;
   private static boolean restartRequested = false;
   private static File workingDir = new File("").getAbsoluteFile();
   private static ClassLoader cl;

   @Inject
   private BeanManager manager;

   public static void main(final String[] args)
   {
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
               boolean restarting = restartRequested;
               restartRequested = false;

               loadPlugins();
               initLogging();
               Weld weld = new Weld();
               // WeldBootstrap bootstrap = new WeldBootstrap();
               WeldContainer container = weld.initialize();
               BeanManager manager = container.getBeanManager();
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
      System.out.println("Loading plugins...");

      try {

         CompositePluginClassLoader composite = new CompositePluginClassLoader(null);

         List<String> installed = InstalledPluginRegistry.getInstalledPlugins();
         ModuleLoader moduleLoader = ModuleLoader.forClassLoader(Bootstrap.class.getClassLoader());
         System.out.println(ModuleLoader.forClassLoader(Bootstrap.class.getClassLoader()).toString());
         moduleLoader = Module.getBootModuleLoader();

         for (String plugin : installed) {
            try {
               System.out.println(plugin);

               Module module = moduleLoader.loadModule(ModuleIdentifier.fromString(plugin));
               // composite.add(module.getClassLoader());

               System.out.println("Loaded: " + module);
            }
            catch (Exception e) {
               System.out.println("Failed loading: " + plugin);
               e.printStackTrace();
            }
         }

         if (cl == null)
            cl = Thread.currentThread().getContextClassLoader();

         // composite.add(cl);
         composite.add(Bootstrap.class.getClassLoader());

         // Thread.currentThread().setContextClassLoader(composite);
      }
      catch (Exception e) {
         e.printStackTrace();
      }
      System.out.println("Done loading plugins...");
   }
}
