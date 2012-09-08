package org.example;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.forge.container.event.PostStartup;
import org.jboss.forge.container.services.Remote;
import org.jboss.forge.container.services.Service;

@Remote
public class ConsumingService
{
   @Inject
   @Service
   private PublishedService service;

   public void postStartup(@Observes PostStartup event)
   {
      System.out.println("ConsumingService has been started. Remote service says [" + service.getMessage() + "]");
   }
}
