/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.bootstrap;


import java.io.File;
import java.util.List;
import java.util.ServiceLoader;
import java.util.logging.Logger;

import org.jboss.forge.addon.manager.AddonManager;
import org.jboss.forge.addon.manager.InstallRequest;
import org.jboss.forge.addon.manager.impl.AddonManagerImpl;
import org.jboss.forge.container.AddonId;
import org.jboss.forge.container.Forge;
import org.jboss.forge.dependencies.spi.DependencyResolver;

/**
 * A class with a main method to bootstrap Forge.
 *
 * You can deploy addons by calling {@link Bootstrap#install(String)}
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public class Bootstrap
{
   private static Logger logger = Logger.getLogger(Bootstrap.class.getName());

   private final Forge forge;
   private final AddonManager addonManager;
   private boolean exitAfter = false;

   public static void main(final String[] args)
   {
      Bootstrap bootstrap = new Bootstrap(args);
      bootstrap.start();
   }

   private Bootstrap(String[] args)
   {
      boolean listInstalled = false;
      String installAddon = null;
      String removeAddon = null;
      forge = ServiceLoader.load(Forge.class).iterator().next();
      if (args.length > 0)
      {
         for (int i = 0; i < args.length; i++)
         {
            if ("--install".equals(args[i]))
            {
               installAddon = args[++i];
            }
            else if ("--remove".equals(args[i]))
            {
               removeAddon = args[++i];
            }
            else if ("--list".equals(args[i]))
            {
               listInstalled = true;
            }
            else if ("--addonDir".equals(args[i]))
            {
               forge.setAddonDir(new File(args[++i]));
            }
            else if ("--batchMode".equals(args[i]))
            {
               forge.setServerMode(false);
            }
            else
               logger.warning("Unknown option: " + args[i]);
         }
      }

      final DependencyResolver dependencyResolver = lookup(DependencyResolver.class);
      addonManager = new AddonManagerImpl(forge.getRepository(), dependencyResolver);

      if (listInstalled)
         list();
      if (installAddon != null)
         install(installAddon);
      if (removeAddon != null)
         remove(removeAddon);
   }

   private void list()
   {
      try
      {
         List<AddonId> addons = forge.getRepository().listEnabled();
         for (AddonId addon : addons)
         {
            logger.info(addon.toCoordinates());
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      finally
      {
         exitAfter = true;
      }
   }

   private void start()
   {
      if (!exitAfter)
         forge.start();
   }

   private void install(String addonCoordinates)
   {
      try
      {
         AddonId addon = AddonId.fromCoordinates(addonCoordinates);
         InstallRequest request = addonManager.install(addon);
         System.out.println(request);
         request.perform();
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      finally
      {
         exitAfter = true;
      }
   }

   private void remove(String addonCoordinates)
   {
      try
      {
         AddonId addon = AddonId.fromCoordinates(addonCoordinates);
         forge.getRepository().disable(addon);
         forge.getRepository().undeploy(addon);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      finally
      {
         exitAfter = true;
      }
   }

   private <T> T lookup(Class<? extends T> service)
   {
      return ServiceLoader.load(service).iterator().next();
   }

}
