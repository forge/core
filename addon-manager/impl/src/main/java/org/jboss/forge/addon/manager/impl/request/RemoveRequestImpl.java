/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.manager.impl.request;

import java.util.concurrent.Callable;

import org.jboss.forge.addon.manager.AddonInfo;
import org.jboss.forge.addon.manager.request.RemoveRequest;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.lock.LockMode;
import org.jboss.forge.furnace.repositories.MutableAddonRepository;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
class RemoveRequestImpl extends AbstractAddonActionRequest implements RemoveRequest
{
   RemoveRequestImpl(AddonInfo addonInfo, MutableAddonRepository repository, Furnace forge)
   {
      super(addonInfo, repository, forge);
   }

   @Override
   public void execute()
   {
      furnace.getLockManager().performLocked(LockMode.WRITE, new Callable<Object>()
      {
         @Override
         public Object call() throws Exception
         {
            AddonId id = getRequestedAddonInfo().getAddon();
            repository.disable(id);
            repository.undeploy(id);
            return null;
         }
      });

   }
}
