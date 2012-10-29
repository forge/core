package org.example.consuming;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.example.published.PublishedService;
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
      System.out.println(getMessage());
   }

   public String getMessage()
   {
      return "I am ConsumingService. Remote service says [" + service.getMessage() + "]";
   }

   public ClassLoader getClassLoader()
   {
      return getClass().getClassLoader();
   }

   public int getRemoteHashCode()
   {
      return service.hashCode();
   }
}
