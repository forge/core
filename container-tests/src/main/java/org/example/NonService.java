package org.example;

import java.io.IOException;

import javax.enterprise.event.Observes;
import javax.inject.Singleton;

import org.jboss.forge.container.event.PostStartup;
import org.jboss.forge.container.event.PreShutdown;
import org.jboss.forge.container.event.Shutdown;
import org.jboss.forge.container.event.Startup;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Singleton
public class NonService
{
   private boolean startupObserved;
   private boolean postStartupObserved;
   private boolean preShutdownObserved;
   private boolean shutdownObserved;

   public void startup(@Observes Startup event) throws IOException
   {
      startupObserved = true;
   }

   public void postStartup(@Observes PostStartup event) throws IOException
   {
      postStartupObserved = true;
   }

   public void preShutdown(@Observes PreShutdown event)
   {
      preShutdownObserved = true;
   }

   public void shutdown(@Observes Shutdown event) throws IOException
   {
      shutdownObserved = true;
   }

   public boolean isStartupObserved()
   {
      return startupObserved;
   }

   public boolean isPostStartupObserved()
   {
      return postStartupObserved;
   }

   public boolean isPreShutdownObserved()
   {
      return preShutdownObserved;
   }

   public boolean isShutdownObserved()
   {
      return shutdownObserved;
   }

}
