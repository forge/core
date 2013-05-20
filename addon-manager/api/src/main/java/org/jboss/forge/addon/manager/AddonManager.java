/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.manager;

import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.repositories.AddonRepository;
import org.jboss.forge.furnace.services.Exported;

/**
 * Responsible for installing, removing, enabling, and disabling {@link AddonId} instances in any registered
 * {@link AddonRepository}.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Exported
public interface AddonManager
{
   /**
    * Create a new {@link InstallRequest} for the given {@link AddonId}.
    */
   public abstract InstallRequest install(AddonId id);

   /**
    * Create a new {@link RemoveRequest} for the given {@link AddonId}.
    */
   public abstract RemoveRequest remove(AddonId id);

   /**
    * Create a new {@link DisableRequest} for the given {@link AddonId}.
    */
   public abstract DisableRequest disable(AddonId id);
}