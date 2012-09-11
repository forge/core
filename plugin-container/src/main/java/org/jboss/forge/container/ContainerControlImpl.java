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
import javax.inject.Singleton;

import org.jboss.forge.container.event.ContainerRestart;
import org.jboss.forge.container.event.ContainerShutdown;
import org.jboss.forge.container.event.ContainerStartup;
import org.jboss.forge.container.event.PostStartup;
import org.jboss.forge.container.event.PreShutdown;
import org.jboss.forge.container.event.Shutdown;
import org.jboss.forge.container.event.Startup;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Singleton
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
      System.out.println("Starting container [" + Thread.currentThread().getName() + "]");
      start();
   }

   void teardown(@Observes ContainerShutdown event)
   {
      System.out.println("Stopping container [" + Thread.currentThread().getName() + "]");
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
      manager.fireEvent(new ContainerRestart());
      stop();
      start();
   }

   public Status getStatus()
   {
      return status;
   }
}
