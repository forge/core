/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.manager.impl;

import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.container.simple.Producer;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.furnace.manager.AddonManager;
import org.jboss.forge.furnace.manager.impl.AddonManagerImpl;
import org.jboss.forge.furnace.manager.spi.AddonDependencyResolver;

/**
 * Produces {@link AddonManager} objects
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class AddonManagerProducer implements Producer<AddonManager>
{
   @Override
   public AddonManager get()
   {
      Furnace furnace = SimpleContainer.getFurnace(getClass().getClassLoader());
      AddonRegistry addonRegistry = furnace.getAddonRegistry();
      AddonDependencyResolver resolver = addonRegistry.getServices(AddonDependencyResolver.class).get();
      return new AddonManagerImpl(furnace, resolver, addonRegistry);
   }
}