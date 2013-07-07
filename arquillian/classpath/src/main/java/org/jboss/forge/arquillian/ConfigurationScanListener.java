/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.arquillian;

import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.exception.ContainerException;
import org.jboss.forge.furnace.spi.ContainerLifecycleListener;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ConfigurationScanListener implements ContainerLifecycleListener
{
   private boolean configurationScanned = false;

   @Override
   public void beforeStart(Furnace furnace) throws ContainerException
   {
   }

   @Override
   public void beforeConfigurationScan(Furnace furnace) throws ContainerException
   {
   }

   @Override
   public void afterConfigurationScan(Furnace furnace) throws ContainerException
   {
      configurationScanned = true;
   }

   @Override
   public void beforeStop(Furnace furnace) throws ContainerException
   {
   }

   @Override
   public void afterStop(Furnace furnace) throws ContainerException
   {
   }

   public boolean isConfigurationScanned()
   {
      return configurationScanned;
   }

}
