/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.manager.impl;

import javax.enterprise.inject.Produces;

import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.manager.AddonManager;
import org.jboss.forge.furnace.manager.impl.AddonManagerImpl;
import org.jboss.forge.furnace.manager.spi.AddonDependencyResolver;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class AddonManagerProducer
{
   @Produces
   public AddonManager produceAddonManager(Furnace furnace, AddonDependencyResolver resolver)
   {
      return new AddonManagerImpl(furnace, resolver);
   }
}
