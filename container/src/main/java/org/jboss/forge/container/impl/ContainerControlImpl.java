/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.container.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.forge.container.ContainerControl;
import org.jboss.forge.container.addons.Status;
import org.jboss.forge.container.event.PostStartup;
import org.jboss.forge.container.event.PreShutdown;
import org.jboss.forge.container.event.Shutdown;
import org.jboss.forge.container.event.Startup;
import org.jboss.forge.container.exception.ContainerException;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Singleton
public class ContainerControlImpl implements ContainerControl
{
   @Inject
   private BeanManager manager;
   private Status status = Status.STOPPED;
   private List<Callable<?>> shutdownListeners = new ArrayList<Callable<?>>();

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
         for (Callable<?> listener : shutdownListeners)
         {
            try
            {
               listener.call();
            }
            catch (Exception e)
            {
               throw new ContainerException("Error invoking shutdown listener [" + listener + "]", e);
            }
         }
         manager.fireEvent(new Shutdown());
         status = Status.STOPPED;
      }
   }

   @Override
   public void restart()
   {
      stop();
      start();
   }

   @Override
   public Status getStatus()
   {
      return status;
   }

   public void registerShutdownListener(Callable<?> callback)
   {
      this.shutdownListeners.add(callback);
   }

   public void removeShutdownListener(Callable<?> callable)
   {
      this.shutdownListeners.remove(callable);
   }
}
