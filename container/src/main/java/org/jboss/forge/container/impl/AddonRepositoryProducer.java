package org.jboss.forge.container.impl;

import java.io.File;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Typed;
import javax.inject.Singleton;

import org.jboss.forge.container.repositories.AddonRepository;

@Singleton
public class AddonRepositoryProducer
{
   private File addonDir;

   @Produces
   @Typed(AddonRepository.class)
   @Singleton
   public AddonRepository produceGlobalAddonRepository()
   {
      return AddonRepositoryImpl.forDirectory(addonDir);
   }

   public void setAddonDir(File addonDir)
   {
      this.addonDir = addonDir;
   }
}
