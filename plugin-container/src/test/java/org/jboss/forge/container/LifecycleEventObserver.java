package org.jboss.forge.container;

import javax.enterprise.event.Observes;
import javax.inject.Singleton;

import org.jboss.forge.container.event.PostStartup;

@Singleton
public class LifecycleEventObserver
{
   private boolean observedPostStartup;

   public void postStartup(@Observes PostStartup event)
   {
      this.observedPostStartup = true;
   }

   public boolean isObservedPostStartup()
   {
      return observedPostStartup;
   }
}
