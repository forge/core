/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.container;

import java.util.List;

import org.jboss.forge.container.addons.AddonRegistry;
import org.jboss.forge.container.repositories.AddonRepository;
import org.jboss.forge.container.spi.ContainerLifecycleListener;
import org.jboss.forge.container.spi.ListenerRegistration;
import org.jboss.forge.container.versions.Version;

public interface Forge
{
   public Forge startAsync();

   public Forge startAsync(ClassLoader loader);

   public Forge start();

   public Forge start(ClassLoader loader);

   public Forge stop();

   public Forge setServerMode(boolean server);

   public AddonRegistry getAddonRegistry();

   public List<AddonRepository> getRepositories();

   public void setRepositories(List<AddonRepository> repositories);

   public Version getVersion();

   public ListenerRegistration<ContainerLifecycleListener> addContainerLifecycleListener(
            ContainerLifecycleListener listener);
}