package test.org.jboss.forge.container;

import javax.enterprise.event.Observes;
import javax.inject.Singleton;

import org.jboss.forge.container.event.PostStartup;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Singleton
public class ContainerLifecycleEventObserver
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
