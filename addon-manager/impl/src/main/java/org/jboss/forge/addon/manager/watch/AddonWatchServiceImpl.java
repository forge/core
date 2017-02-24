/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.manager.watch;

import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.jboss.forge.addon.manager.watch.AddonWatchService;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.resource.monitor.ResourceMonitor;
import org.jboss.forge.furnace.addons.Addon;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.container.simple.AbstractEventListener;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.furnace.manager.AddonManager;
import org.jboss.forge.furnace.repositories.MutableAddonRepository;
import org.jboss.forge.furnace.util.OperatingSystemUtils;
import org.jboss.forge.furnace.versions.Versions;

/**
 * Service to monitor addon changes and auto-redeploy
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class AddonWatchServiceImpl extends AbstractEventListener implements AddonWatchService
{
   private final Map<AddonId, ResourceMonitor> monitors = new ConcurrentHashMap<>();

   private AddonRegistry addonRegistry;
   private AddonManager addonManager;
   private ResourceFactory resourceFactory;

   @Override
   @SuppressWarnings("unchecked")
   public void start()
   {
      getAddonRegistry()
               .getAddons(addon -> Versions.isSnapshot(addon.getId().getVersion())
                        && addon.getRepository() instanceof MutableAddonRepository)
               .stream()
               .map(Addon::getId)
               .forEach(addonId -> {
                  // Find local repository path for each addon
                  File installationPath = getInstallationPathFor(addonId);
                  FileResource<?> resource = getResourceFactory().create(FileResource.class, installationPath);
                  ResourceMonitor monitor = resource.monitor();
                  monitor.addResourceListener(e -> {
                     // Run addonManager.remove and addonManager.install
                     getAddonManager().remove(addonId).perform();
                     getAddonManager().install(addonId).perform();
                  });
                  monitors.put(addonId, monitor);
               });
   }

   @Override
   public boolean isStarted()
   {
      return monitors.size() > 0;
   }

   @Override
   protected void handleThisPreShutdown()
   {
      stop();
   }

   @Override
   public void stop()
   {
      for (ResourceMonitor monitor : monitors.values())
      {
         monitor.cancel();
      }
      monitors.clear();
   }

   @Override
   public Set<AddonId> getMonitoredAddons()
   {
      return Collections.unmodifiableSet(monitors.keySet());
   }

   static File getInstallationPathFor(AddonId addonId)
   {
      String name = addonId.getName();
      // TODO: Read from settings.xml?
      StringBuilder sb = new StringBuilder(OperatingSystemUtils.getUserHomePath()).append("/.m2/repository/");
      sb.append(name.replace('.', '/').replace(':', '/'));
      sb.append("/").append(addonId.getVersion());
      sb.append("/").append(name.substring(name.lastIndexOf(":") + 1)).append("-").append(addonId.getVersion())
               .append(".jar");
      return new File(sb.toString());
   }

   /**
    * @return the addonRegistry
    */
   private AddonRegistry getAddonRegistry()
   {
      if (addonRegistry == null)
      {
         this.addonRegistry = SimpleContainer.getFurnace(getClass().getClassLoader()).getAddonRegistry();
      }
      return addonRegistry;
   }

   /**
    * @return the addonManager
    */
   private AddonManager getAddonManager()
   {
      if (addonManager == null)
      {
         this.addonManager = getAddonRegistry().getServices(AddonManager.class).get();
      }
      return addonManager;
   }

   /**
    * @return the resourceFactory
    */
   private ResourceFactory getResourceFactory()
   {
      if (resourceFactory == null)
      {
         this.resourceFactory = getAddonRegistry().getServices(ResourceFactory.class).get();
      }
      return resourceFactory;
   }
}
