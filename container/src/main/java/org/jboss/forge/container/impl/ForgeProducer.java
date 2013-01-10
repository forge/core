package org.jboss.forge.container.impl;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Typed;
import javax.inject.Singleton;

import org.jboss.forge.container.Forge;

@Singleton
public class ForgeProducer
{
   private Forge forge;

   @Produces
   @Typed(Forge.class)
   @Singleton
   public Forge produceForge()
   {
      return forge;
   }

   public void setForge(Forge forge)
   {
      this.forge = forge;
   }
}
