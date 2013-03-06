/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.container.spi;

import java.util.ServiceLoader;

import org.jboss.forge.container.Forge;
import org.jboss.forge.container.exception.ContainerException;

/**
 * Listens for actions in a Forge container
 *
 * Listeners should be registered using the Service Provider mechanism
 *
 * @see ServiceLoader
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public interface ContainerLifecycleListener
{
   /**
    * Called before the Forge container starts
    */
   public void beforeStart(Forge forge) throws ContainerException;

   /**
    * Called before the Forge container stops
    */
   public void beforeStop(Forge forge) throws ContainerException;

   /**
    * Called after Forge container stops
    */
   public void afterStop(Forge forge) throws ContainerException;
}
