package org.jboss.forge.container.impl.repository;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Typed;
import javax.inject.Singleton;

import org.jboss.forge.container.repositories.AddonRepository;

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

   public void setRespository(AddonRepository repository)
   {
      this.repository = repository;
      ;
   }
}
