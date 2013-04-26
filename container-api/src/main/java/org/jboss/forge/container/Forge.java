/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.container;

import java.io.File;
import java.util.List;

import org.jboss.forge.container.addons.Addon;
import org.jboss.forge.container.addons.AddonRegistry;
import org.jboss.forge.container.lock.LockManager;
import org.jboss.forge.container.repositories.AddonRepository;
import org.jboss.forge.container.repositories.AddonRepositoryMode;
import org.jboss.forge.container.spi.ContainerLifecycleListener;
import org.jboss.forge.container.spi.ListenerRegistration;
import org.jboss.forge.container.versions.Version;

/**
 * Operations for initializing, starting, interacting with, and stopping a {@link Forge} container.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface Forge
{
   /**
    * Start this {@link Forge} instance in a new background {@link Thread}.
    */
   public Forge startAsync();

   /**
    * Start this {@link Forge} instance in a new background {@link Thread}, using the given {@link ClassLoader} to load
    * core implementation resources.
    */
   public Forge startAsync(ClassLoader loader);

   /**
    * Start this {@link Forge} instance and wait for completion.
    */
   public Forge start();

   /**
    * Start this {@link Forge} instance and wait for completion, using the given {@link ClassLoader} to load core
    * implementation resources.
    */
   public Forge start(ClassLoader loader);

   /**
    * Stop this {@link Forge} instance.
    */
   public Forge stop();

   /**
    * When server mode is set to <code>true</code>, {@link Forge} will run as a server process until explicitly stopped.
    * When server mode is false, {@link Forge} will process all inputs and exit once addons have completed any requested
    * operations.
    */
   public Forge setServerMode(boolean server);

   /**
    * Get the central {@link AddonRegistry} for this {@link Forge} instance. Contains {@link Addon} registration and
    * service information.
    */
   public AddonRegistry getAddonRegistry();

   /**
    * Get an immutable {@link List} of configured {@link AddonRepository} instances.
    */
   public List<AddonRepository> getRepositories();

   /**
    * Add an {@link AddonRepository} to be scanned for deployed and enabled {@link Addon} instances. This method must
    * not be called once {@link Forge} is started.
    */
   public AddonRepository addRepository(AddonRepositoryMode mode, File repository);

   /**
    * Get the current runtime API version of {@link Forge}.
    */
   public Version getVersion();

   /**
    * Register a {@link ContainerLifecycleListener} instance. Returns a {@link ListenerRegistration} that can be used to
    * un-register the listener.
    */
   public ListenerRegistration<ContainerLifecycleListener> addContainerLifecycleListener(
            ContainerLifecycleListener listener);

   /**
    * Get the {@link ClassLoader} from which {@link Forge} loaded its internal classes.
    */
   public ClassLoader getRuntimeClassLoader();

   /**
    * Get the {@link LockManager} associated with this {@link Forge} instance
    */
   public LockManager getLockManager();

   /**
    * Get the current status of this {@link Forge} container
    */
   public ContainerStatus getStatus();

   /**
    * Set the arguments with which {@link Forge} should start. Typically this will simply be passed through from
    * <code>public static void main(String[] args)</code>.
    */
   public void setArgs(String[] args);

   /**
    * Get the arguments with which {@link Forge} should start. Typically this will simply be passed through from
    * <code>public static void main(String[] args)</code>.
    * 
    * @return the arguments, or <code>null</code> if no arguments were set.
    */
   public String[] getArgs();

}