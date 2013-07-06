package org.jboss.forge.arquillian;

import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.exception.ContainerException;
import org.jboss.forge.furnace.spi.ContainerLifecycleListener;

public class ConfigurationScanListener implements ContainerLifecycleListener
{
   private boolean configurationScanned = false;

   @Override
   public void beforeStart(Furnace forge) throws ContainerException
   {
   }

   @Override
   public void beforeConfigurationScan(Furnace forge) throws ContainerException
   {
   }

   @Override
   public void afterConfigurationScan(Furnace forge) throws ContainerException
   {
      configurationScanned = true;
   }

   @Override
   public void beforeStop(Furnace forge) throws ContainerException
   {
   }

   @Override
   public void afterStop(Furnace forge) throws ContainerException
   {
   }

   public boolean isConfigurationScanned()
   {
      return configurationScanned;
   }

}
