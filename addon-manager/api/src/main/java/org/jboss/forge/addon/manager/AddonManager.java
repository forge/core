/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.manager;

import org.jboss.forge.addon.manager.request.DeployRequest;
import org.jboss.forge.addon.manager.request.DisableRequest;
import org.jboss.forge.addon.manager.request.EnableRequest;
import org.jboss.forge.addon.manager.request.InstallRequest;
import org.jboss.forge.addon.manager.request.RemoveRequest;
import org.jboss.forge.addon.manager.spi.AddonInfo;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.repositories.AddonRepository;
import org.jboss.forge.furnace.services.Exported;

/**
 * Responsible for installing, removing, enabling, and disabling {@link AddonId} instances in any registered
 * {@link AddonRepository}.
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Exported
public interface AddonManager
{
   /**
    * @param id the addon to be informed
    * @return information about the {@link AddonId}, like required and optional addons that this addon depends on.
    */
   AddonInfo info(AddonId addonId);

   /**
    * Create a new {@link InstallRequest} for the given {@link AddonId}.
    * 
    * @param id the addon to be installed
    * @return the request for installation
    */
   InstallRequest install(AddonId id);

   /**
    * Create a new {@link InstallRequest} for the given {@link AddonId} and {@link AddonRepository}.
    * 
    * @param id the addon to be installed
    * @param addonRepository the {@link AddonRepository} to be used
    * @return the request for installation
    */
   InstallRequest install(AddonId id, AddonRepository addonRepository);

   /**
    * Create a new {@link DeployRequest} for the given {@link AddonId}.
    * 
    * @param id the addon to be installed
    * @return the request for installation
    */
   DeployRequest deploy(AddonId id);

   /**
    * Create a new {@link DeployRequest} for the given {@link AddonId} and {@link AddonRepository}.
    * 
    * @param id the addon to be deployed
    * @param addonRepository the {@link AddonRepository} to be used
    * @return the request for installation
    */
   DeployRequest deploy(AddonId id, AddonRepository addonRepository);

   /**
    * Create a new {@link RemoveRequest} for the given {@link AddonId}.
    * 
    * @param id the addon to be removed
    * @return the request for removal
    */
   RemoveRequest remove(AddonId id);

   /**
    * Create a new {@link RemoveRequest} for the given {@link AddonId} and {@link AddonRepository}.
    * 
    * @param id the addon to be removed
    * @return the request for removal
    */
   RemoveRequest remove(AddonId id, AddonRepository addonRepository);

   /**
    * Create a new {@link EnableRequest} for the given {@link AddonId}.
    * 
    * @param id the addon to be enabled
    * @return the request for activation
    */
   EnableRequest enable(AddonId id);

   /**
    * Create a new {@link EnableRequest} for the given {@link AddonId} and {@link AddonRepository}.
    * 
    * @param id the addon to be enabled
    * @return the request for activation
    */
   EnableRequest enable(AddonId id, AddonRepository addonRepository);

   /**
    * Create a new {@link DisableRequest} for the given {@link AddonId}.
    * 
    * @param id the addon to be disabled
    * @return the request for de-activation
    */
   DisableRequest disable(AddonId id);

   /**
    * Create a new {@link DisableRequest} for the given {@link AddonId} and {@link AddonRepository}.
    * 
    * @param id the addon to be disabled
    * @return the request for de-activation
    */
   DisableRequest disable(AddonId id, AddonRepository addonRepository);

}