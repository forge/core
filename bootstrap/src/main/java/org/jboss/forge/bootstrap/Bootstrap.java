/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.bootstrap;

import java.util.ServiceLoader;
import java.util.logging.Logger;

import org.jboss.forge.addon.dependency.spi.DependencyResolver;
import org.jboss.forge.addon.manager.AddonManager;
import org.jboss.forge.addon.manager.InstallRequest;
import org.jboss.forge.container.AddonId;
import org.jboss.forge.container.Forge;

/**
 * A class with a main method to bootstrap Forge.
 * 
 * You can deploy addons by calling {@link Bootstrap#deploy(String)}
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public class Bootstrap
{
   static Logger logger = Logger.getLogger(Bootstrap.class.getName());

   private Forge forge;

   public static void main(final String[] args)
   {
      Bootstrap bootstrap = new Bootstrap();
      logger.info("Starting Forge embedded ...");
      bootstrap.start();
   }

   public Bootstrap()
   {
      forge = new Forge();
   }

   public void start()
   {
      forge.start();
   }

   public void deploy(String addonCoordinates)
   {
      AddonId addon = AddonId.fromCoordinates(addonCoordinates);
      DependencyResolver dependencyResolver = lookup(DependencyResolver.class);
      AddonManager addonManager = new AddonManager(forge.getRepository(), dependencyResolver);
      InstallRequest request = addonManager.install(addon);
      request.perform();
   }

   private <T> T lookup(Class<? extends T> service)
   {
      return ServiceLoader.load(service).iterator().next();
   }

}
