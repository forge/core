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
   public Forge startAsync();

   public Forge startAsync(ClassLoader loader);

   public Forge start();

   public Forge start(ClassLoader loader);

   public Forge stop();

   public Forge setServerMode(boolean server);

   public Forge setAddonDir(File dir);

   public File getAddonDir();

   public AddonRegistry getAddonRegistry();

   public AddonRepository getRepository();

   public String getVersion();

   public Forge addContainerLifecycleListener(ContainerLifecycleListener listener);
}