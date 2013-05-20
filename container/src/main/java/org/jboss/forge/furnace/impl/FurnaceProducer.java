package org.jboss.forge.furnace.impl;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Typed;
import javax.inject.Singleton;

import org.jboss.forge.furnace.Furnace;

@Singleton
public class FurnaceProducer
{
   private Furnace forge;

   @Produces
   @Typed(Furnace.class)
   @Singleton
   public Furnace produceForge()
   {
      return forge;
   }

   public void setForge(Furnace forge)
   {
      this.forge = forge;
   }
}
