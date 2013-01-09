package org.example;

import javax.inject.Inject;

import org.jboss.forge.container.services.Remote;

@Remote
public class ConsumingService
{
   @Inject
   private PublisherService service;

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
