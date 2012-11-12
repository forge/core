package org.example.event;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;

public class EventResponseService
{
   @Inject
   @Named("2")
   private Event<Object> response;

   public void postStartup(@Observes @Named("1") Object event)
   {
      System.out.println("Observed: ****** " + event + " in " + Thread.currentThread().getContextClassLoader());
      response.fire(this);
   }
}
