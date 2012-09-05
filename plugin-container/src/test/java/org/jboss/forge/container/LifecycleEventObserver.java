package org.jboss.forge.container;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import org.jboss.forge.container.event.PostStartup;
import org.jboss.forge.container.services.Service;

@Singleton
public class LifecycleEventObserver
{
   private boolean observedPostStartup;

   @Produces
   @Service
   public TestRemote produce()
   {
      return null;
   }

   public void postStartup(@Observes PostStartup event)
   {
      this.observedPostStartup = true;
   }

   public boolean isObservedPostStartup()
   {
      return observedPostStartup;
   }
}
