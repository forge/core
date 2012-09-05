package org.jboss.forge.container;

import org.jboss.forge.container.services.ServiceType;

/**
 * Controls the life-cycle of a Forge {@link ServiceType} container
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
}
