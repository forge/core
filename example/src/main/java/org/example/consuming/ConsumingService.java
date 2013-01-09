package org.example.consuming;

import javax.inject.Inject;

import org.example.other.OtherExampleAddon;
import org.jboss.forge.container.services.Remote;
import org.jboss.forge.container.services.Service;

@Remote
public class ConsumingService
{
   @Inject
   @Service
   private OtherExampleAddon service;

   public int getRemoteHashCode()
   {
      return service.hashCode();
   }
}
