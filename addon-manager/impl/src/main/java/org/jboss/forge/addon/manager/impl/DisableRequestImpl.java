/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.manager.impl;

import org.jboss.forge.addon.manager.AddonManager;
import org.jboss.forge.addon.manager.DisableRequest;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.repositories.AddonRepository;
import org.jboss.forge.furnace.repositories.MutableAddonRepository;
import org.jboss.forge.furnace.util.Assert;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class DisableRequestImpl implements DisableRequest
{
   @SuppressWarnings("unused")
   private AddonManager manager;
   
   private Furnace forge;
   private AddonId id;

   public DisableRequestImpl(AddonManager manager, Furnace forge, AddonId id)
   {
      Assert.notNull(manager, "AddonManager must not be null.");
      Assert.notNull(forge, "Furnace must not be null.");
      Assert.notNull(id, "AddonId must not be null.");
      this.manager = manager;
      this.forge = forge;
      this.id = id;
   }

   @Override
   public AddonId getAddonIdToDisable()
   {
      return null;
   }

   @Override
   public void perform()
   {
      for (AddonRepository repository : forge.getRepositories())
      {
         if (repository instanceof MutableAddonRepository && repository.isEnabled(id))
            ((MutableAddonRepository) repository).disable(id);
      }
   }

   @Override
   public void perform(AddonRepository repository)
   {
      if (repository instanceof MutableAddonRepository)
         ((MutableAddonRepository) repository).disable(id);
      else
         throw new IllegalArgumentException(
                  "Cannot remove addons from an immutable AddonRepository (must implement MutableAddonRepository.");
   }

}
