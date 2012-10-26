package org.jboss.forge.container;

import org.jboss.forge.container.services.RemoteInstance;

/**
 * Controls the life-cycle of a Forge {@link RemoteInstance} container
 */
public interface ContainerControl
{
   /**
    * Start the container.
    */
   void start();

   /**
    * Stop the container.
    */
   void stop();

   /**
    * Restart the container.
    */
   void restart();

   /**
    * Get the {@link Status} of the container.
    * 
    * @return
    */
   Status getStatus();
}
