/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.container;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.jboss.forge.container.event.ContainerShutdown;
import org.jboss.forge.container.event.ContainerStartup;
import org.jboss.forge.container.event.PostStartup;
import org.jboss.forge.container.event.PreShutdown;
import org.jboss.forge.container.event.Restart;
import org.jboss.forge.container.event.Shutdown;
import org.jboss.forge.container.event.Startup;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ContainerControlImpl implements ContainerControl
{
   @Inject
   private BeanManager manager;
   private Status status = Status.STOPPED;

   public enum Status
   {
      STARTING, STARTED, STOPPING, STOPPED
   }

   void bootstrap(@Observes ContainerStartup event)
   {
      System.out.println("Starting container.");
      start();
   }

   void teardown(@Observes ContainerShutdown event)
   {
      System.out.println("Stopping container.");
      stop();
   }

   @Override
   public void start()
   {
      if (Status.STOPPED.equals(status))
      {
         status = Status.STARTING;
         manager.fireEvent(new Startup());
         status = Status.STARTED;
         manager.fireEvent(new PostStartup());
      }
      /*
       * One classloader/thread/weld container per plugin module. One primary executor container running, fires events
       * to each plugin-container.
       * 
       * Multi-threaded bootstrap. Loads primary container, then attaches individual plugin containers as they come up.
       * 
       * Prevents weld library conflicts.
       * 
       * Ideas:
       * 
       * Plugins may depend on plugins, but that effectively disables the requested container and merges it with the
       * requesting container, which now contains classes from both containers.
       */
   }

   @Override
   public void stop()
   {
      if (Status.STARTED.equals(status))
      {
         manager.fireEvent(new PreShutdown());
         status = Status.STOPPING;
         manager.fireEvent(new Shutdown());
         status = Status.STOPPED;
      }
   }

   @Override
   public void restart()
   {
      manager.fireEvent(new Restart());
      stop();
      start();
   }

   public Status getStatus()
   {
      return status;
   }
}
