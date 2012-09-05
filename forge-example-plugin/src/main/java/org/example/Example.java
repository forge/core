package org.example;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.forge.container.event.PostStartup;
import org.jboss.forge.container.services.ServiceRegistry;

public class Example
{
   @Inject
   private ServiceRegistry registry;

   public ServiceRegistry getRegistry()
   {
      return registry;
   }

   public void postStartup(@Observes PostStartup event)
   {
      System.out.println("Observed PostStartup");
   }
}
