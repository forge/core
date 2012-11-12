package org.example.event;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Singleton
public class EventService
{
   @Inject
   @Named("1")
   private Event<Object> request;

   private boolean responseRecieved = false;

   public void fire()
   {
      request.fire(this);
   }

   public void handleResponse(@Observes @Named("2") Object event)
   {
      System.out.println("Response: ****** " + event + " in " + Thread.currentThread().getContextClassLoader());
      responseRecieved = true;
   }

   public boolean isResponseRecieved()
   {
      return responseRecieved;
   }

   @Override
   public String toString()
   {
      return "EventService";
   }

}
