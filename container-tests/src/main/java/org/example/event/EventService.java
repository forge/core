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

   private boolean localRequestRecieved = false;
   private boolean remoteResponseRecieved = false;
   private boolean wrongResponseRecieved = false;

   public void fire()
   {
      request.fire(new EventPayload1());
   }

   public void fireNonRemote()
   {
      request.fire(new EventPayload2());
   }

   public void handleLocalRequest(@Observes @Named("1") Object event)
   {
      localRequestRecieved = true;
   }

   public void handleRemoteResponse(@Observes @Named("2") Object event)
   {
      remoteResponseRecieved = true;
   }

   public void handleWrongResponse(@Observes @Named("3") Object event)
   {
      wrongResponseRecieved = true;
   }

   public boolean isLocalRequestRecieved()
   {
      return localRequestRecieved;
   }

   public boolean isRemoteResponseRecieved()
   {
      return remoteResponseRecieved;
   }

   public boolean isWrongResponseRecieved()
   {
      return wrongResponseRecieved;
   }

   @Override
   public String toString()
   {
      return "EventService";
   }

}
