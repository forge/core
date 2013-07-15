/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.manager.impl.request;

import java.util.logging.Logger;

import org.jboss.forge.addon.manager.request.AddonActionRequest;
import org.jboss.forge.addon.manager.spi.AddonInfo;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.repositories.MutableAddonRepository;
import org.jboss.forge.furnace.spi.ContainerLifecycleListener;
import org.jboss.forge.furnace.spi.ListenerRegistration;
import org.jboss.forge.furnace.util.Assert;

/**
 * Abstract class for {@link AddonActionRequest} implementations
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public abstract class AbstractAddonActionRequest implements AddonActionRequest
{
   protected final AddonInfo addonInfo;
   protected final Furnace furnace;
   protected final MutableAddonRepository repository;

   protected Logger log = Logger.getLogger(getClass().getName());

   protected AbstractAddonActionRequest(AddonInfo addonInfo, MutableAddonRepository addonRepository, Furnace furnace)
   {
      Assert.notNull(addonInfo, "AddonInfo must not be null.");
      Assert.notNull(furnace, "Addon Repository must not be null.");
      Assert.notNull(furnace, "Furnace must not be null.");
      this.addonInfo = addonInfo;
      this.furnace = furnace;
      this.repository = addonRepository;
   }

   @Override
   public final AddonInfo getRequestedAddonInfo()
   {
      return addonInfo;
   }

   @Override
   public final void perform()
   {
      ConfigurationScanListener listener = new ConfigurationScanListener();
      ListenerRegistration<ContainerLifecycleListener> reg = furnace.addContainerLifecycleListener(listener);
      try
      {
         execute();
         if (!furnace.getStatus().isStopped())
         {
            while (furnace.getStatus().isStarting() || !listener.isConfigurationScanned())
            {
               try
               {
                  Thread.sleep(100);
               }
               catch (InterruptedException e)
               {
                  throw new RuntimeException(e);
               }
            }
         }
      }
      finally
      {
         reg.removeListener();
      }
   }

   public abstract void execute();

   @Override
   public String toString()
   {
      return getClass().getSimpleName() + ":[" + getRequestedAddonInfo() + "]";
   }

}
