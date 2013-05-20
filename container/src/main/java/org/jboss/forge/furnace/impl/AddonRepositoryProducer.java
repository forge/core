package org.jboss.forge.furnace.impl;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Typed;
import javax.inject.Singleton;

import org.jboss.forge.furnace.repositories.AddonRepository;

@Singleton
public class AddonRepositoryProducer
{
   private AddonRepository repository;

   @Produces
   @Singleton
   @Typed(AddonRepository.class)
   public AddonRepository produceGlobalAddonRepository()
   {
      return repository;
   }

   public void setRepository(AddonRepository repository)
   {
      this.repository = repository;
   }
}
