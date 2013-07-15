/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.manager.impl.request;

import java.util.List;

import org.jboss.forge.addon.manager.request.AddonActionRequest;
import org.jboss.forge.addon.manager.request.DeployRequest;
import org.jboss.forge.addon.manager.request.DisableRequest;
import org.jboss.forge.addon.manager.request.EnableRequest;
import org.jboss.forge.addon.manager.request.InstallRequest;
import org.jboss.forge.addon.manager.request.RemoveRequest;
import org.jboss.forge.addon.manager.request.UpdateRequest;
import org.jboss.forge.addon.manager.spi.AddonInfo;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.repositories.MutableAddonRepository;

/**
 * Creates {@link AddonActionRequest} objects
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public class AddonActionRequestFactory
{
   public static InstallRequest createInstallRequest(AddonInfo addonInfo, List<AddonActionRequest> actions)
   {
      return new InstallRequestImpl(addonInfo, actions);
   }

   public static DeployRequest createDeployRequest(AddonInfo addonInfo, MutableAddonRepository repository,
            Furnace furnace)
   {
      return new DeployRequestImpl(addonInfo, repository, furnace);
   }

   public static UpdateRequest createUpdateRequest(AddonInfo addonToRemove, AddonInfo addonToInstall,
            MutableAddonRepository repository, Furnace furnace)
   {
      RemoveRequest removeRequest = createRemoveRequest(addonToRemove, repository, furnace);
      DeployRequest installRequest = createDeployRequest(addonToInstall, repository, furnace);
      return new UpdateRequestImpl(removeRequest, installRequest);
   }

   public static RemoveRequest createRemoveRequest(AddonInfo addonInfo, MutableAddonRepository repository,
            Furnace furnace)
   {
      return new RemoveRequestImpl(addonInfo, repository, furnace);
   }

   public static EnableRequest createEnableRequest(AddonInfo addonInfo, MutableAddonRepository repository,
            Furnace furnace)
   {
      return new EnableRequestImpl(addonInfo, repository, furnace);
   }

   public static DisableRequest createDisableRequest(AddonInfo addonInfo, MutableAddonRepository repository,
            Furnace furnace)
   {
      return new DisableRequestImpl(addonInfo, repository, furnace);
   }
}
