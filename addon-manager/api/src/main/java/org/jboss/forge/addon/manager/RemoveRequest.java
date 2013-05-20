/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.manager;

import org.jboss.forge.furnace.addons.Addon;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.repositories.AddonRepository;
import org.jboss.forge.furnace.repositories.MutableAddonRepository;

/**
 * This object is responsible for removing an {@link Addon}.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface RemoveRequest
{
   /**
    * The {@link AddonId} to remove.
    */
   public abstract AddonId getAddonIdToRemove();

   /**
    * This will remove the {@link AddonId} from all registered {@link MutableAddonRepository} instances.
    */
   public abstract void perform();

   /**
    * This will remove the {@link AddonId} from the given {@link MutableAddonRepository} instances.
    */
   public abstract void perform(AddonRepository target);
}