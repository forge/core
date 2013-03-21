package org.example;

import java.io.IOException;

import javax.enterprise.event.Observes;
import javax.inject.Singleton;

import org.jboss.forge.container.event.Startup;
import org.jboss.forge.container.event.PreShutdown;
import org.jboss.forge.container.services.Exported;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Exported
@Singleton
public class LifecycleListenerService
{
   private boolean performObserved;
   private boolean preShutdownObserved;

   public void perform(@Observes Startup event) throws IOException
   {
      performObserved = true;
   }

   public void preShutdown(@Observes PreShutdown event)
   {
      preShutdownObserved = true;
   }

   public boolean isPerformObserved()
   {
      return performObserved;
   }

   public boolean isPreShutdownObserved()
   {
      return preShutdownObserved;
   }
}
