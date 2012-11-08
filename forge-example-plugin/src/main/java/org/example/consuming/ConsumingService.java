package org.example.consuming;

import javax.inject.Inject;

import org.example.published.PublishedService;
import org.jboss.forge.container.services.Remote;
import org.jboss.forge.container.services.Service;

@Remote
public class ConsumingService
{
   @Inject
   @Service
   private PublishedService service;

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
