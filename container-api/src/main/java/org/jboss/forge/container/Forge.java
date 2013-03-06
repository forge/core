/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.container;

import java.io.File;

import org.jboss.forge.container.spi.ContainerLifecycleListener;

public interface Forge
{
   public abstract Forge startAsync();

   public abstract Forge startAsync(ClassLoader loader);

   public abstract Forge start();

   public abstract Forge start(ClassLoader loader);

   public abstract Forge stop();

   public abstract AddonRegistry getAddonRegistry();

   public abstract Forge setAddonDir(File dir);

   public abstract Forge setServerMode(boolean server);

   public abstract File getAddonDir();

   public abstract AddonRepository getRepository();

   public abstract Forge registerContainerLifecycleListener(ContainerLifecycleListener listener);

   public abstract Forge unregisterContainerLifecycleListener(ContainerLifecycleListener listener);

   public abstract String getVersion();
}