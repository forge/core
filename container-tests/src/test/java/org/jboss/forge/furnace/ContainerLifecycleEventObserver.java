package org.jboss.forge.furnace;

import javax.enterprise.event.Observes;
import javax.inject.Singleton;

import org.jboss.forge.furnace.event.PostStartup;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Singleton
public class ContainerLifecycleEventObserver
{
   private boolean observedPerform;

   public void perform(@Observes PostStartup event)
   {
      this.observedPerform = true;
   }

   public boolean isObservedPerform()
   {
      return observedPerform;
   }
}
