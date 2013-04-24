/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.manager;

import org.jboss.forge.container.addons.Addon;
import org.jboss.forge.container.addons.AddonId;
import org.jboss.forge.container.repositories.AddonRepository;
import org.jboss.forge.container.repositories.MutableAddonRepository;

/**
 * This object is responsible for disabling an {@link Addon}.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface DisableRequest
{
   /**
    * The {@link AddonId} to disable.
    */
   public abstract AddonId getAddonIdToDisable();

   /**
    * This will disable the {@link AddonId} in all registered {@link MutableAddonRepository} instances.
    */
   public abstract void perform();

   /**
    * This will disable the {@link AddonId} in the given {@link MutableAddonRepository} instances.
    */
   public abstract void perform(AddonRepository target);

}