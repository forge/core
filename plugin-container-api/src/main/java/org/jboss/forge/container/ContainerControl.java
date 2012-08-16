package org.jboss.forge.container;

import org.jboss.forge.container.plugin.Plugin;

/**
 * Controls the life-cycle of a forge {@link Plugin} container
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
