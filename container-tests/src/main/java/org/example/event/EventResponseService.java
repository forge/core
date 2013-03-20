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

   public void observeFirst(@Observes @Named("1") Object event)
   {
      response.fire(new EventPayload3());
   }
}
