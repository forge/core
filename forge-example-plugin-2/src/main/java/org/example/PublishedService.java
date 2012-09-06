package org.example;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Typed;

import org.jboss.forge.container.services.Remote;

@Typed()
public class PublishedService implements Remote
{
   public String getMessage()
   {
      return "Hello from PublishedService";
   }

   @Produces
   @Typed({ PublishedService.class, Remote.class })
   public PublishedService produce()
   {
      return new PublishedService();
   }
}
