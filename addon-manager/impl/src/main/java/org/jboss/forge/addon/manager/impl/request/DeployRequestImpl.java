/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.manager.impl.request;

import java.io.File;
import java.util.Set;
import java.util.concurrent.Callable;

import org.jboss.forge.addon.manager.request.DeployRequest;
import org.jboss.forge.addon.manager.spi.AddonInfo;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.lock.LockMode;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.furnace.repositories.MutableAddonRepository;

/**
 * When an addon is installed, another addons could be required. This object returns the necessary information for the
 * installation of an addon to succeed, like required addons and dependencies
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
class DeployRequestImpl extends AbstractAddonActionRequest implements DeployRequest
{
   /**
    * Package-access constructor. Only AddonManager should be allowed to call this constructor.
    * 
    * @param addonManager
    */
   DeployRequestImpl(AddonInfo info, MutableAddonRepository repository, Furnace forge)
   {
      super(info, repository, forge);
   }

   @Override
   public void execute()
   {
      furnace.getLockManager().performLocked(LockMode.WRITE, new Callable<Object>()
      {
         @Override
         public Object call() throws Exception
         {
            deploy(repository);
            repository.enable(addonInfo.getAddon());
            return null;
         }
      });
   }

   protected void deploy(MutableAddonRepository repository)
   {
      AddonId addon = addonInfo.getAddon();
      Set<File> resourceJars = addonInfo.getResources();

      if (resourceJars.isEmpty())
      {
         log.fine("No resource JARs found for " + addon);
      }

      Set<AddonDependencyEntry> addonDependencies = addonInfo.getDependencyEntries();
      if (addonDependencies.isEmpty())
      {
         log.fine("No dependencies found for addon " + addon);
      }
      log.info("Deploying addon " + addon);
      repository.deploy(addon, addonDependencies, resourceJars);
   }
}