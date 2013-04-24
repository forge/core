/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.manager.impl;

import org.jboss.forge.addon.manager.AddonManager;
import org.jboss.forge.addon.manager.RemoveRequest;
import org.jboss.forge.container.Forge;
import org.jboss.forge.container.addons.AddonId;
import org.jboss.forge.container.repositories.AddonRepository;
import org.jboss.forge.container.repositories.MutableAddonRepository;
import org.jboss.forge.container.util.Assert;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class RemoveRequestImpl implements RemoveRequest
{
   @SuppressWarnings("unused")
   private AddonManager manager;
   
   private Forge forge;
   private AddonId id;

   public RemoveRequestImpl(AddonManager manager, Forge forge, AddonId id)
   {
      Assert.notNull(manager, "AddonManager must not be null.");
      Assert.notNull(forge, "Forge must not be null.");
      Assert.notNull(id, "AddonId must not be null.");
      this.manager = manager;
      this.forge = forge;
      this.id = id;
   }

   @Override
   public AddonId getAddonIdToRemove()
   {
      return id;
   }

   @Override
   public void perform()
   {
      for (AddonRepository repository : forge.getRepositories())
      {
         if (repository instanceof MutableAddonRepository && repository.isEnabled(id))
            ((MutableAddonRepository) repository).undeploy(id);
      }
   }

   @Override
   public void perform(AddonRepository repository)
   {
      if (repository instanceof MutableAddonRepository)
         ((MutableAddonRepository) repository).undeploy(id);
      else
         throw new IllegalArgumentException("Cannot remove addons from an immutable AddonRepository (must implement MutableAddonRepository.");
   }

}
