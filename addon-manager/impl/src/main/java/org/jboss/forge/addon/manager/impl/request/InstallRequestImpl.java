/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.manager.impl.request;

import java.util.Collections;
import java.util.List;

import org.jboss.forge.addon.manager.AddonInfo;
import org.jboss.forge.addon.manager.request.AddonActionRequest;
import org.jboss.forge.addon.manager.request.InstallRequest;

/**
 * Implementation of the {@link InstallRequest} interface
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
class InstallRequestImpl implements InstallRequest
{
   private final AddonInfo addonInfo;
   private final List<AddonActionRequest> actions;

   public InstallRequestImpl(AddonInfo addonInfo, List<AddonActionRequest> actions)
   {
      this.addonInfo = addonInfo;
      this.actions = Collections.unmodifiableList(actions);
   }

   @Override
   public void perform()
   {
      for (AddonActionRequest action : actions)
      {
         action.perform();
      }
   }

   @Override
   public List<AddonActionRequest> getActions()
   {
      return actions;
   }

   @Override
   public AddonInfo getRequestedAddonInfo()
   {
      return addonInfo;
   }

}
