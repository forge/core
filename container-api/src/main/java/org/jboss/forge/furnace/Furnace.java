/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.furnace;

import java.io.File;
import java.util.List;

import org.jboss.forge.furnace.addons.Addon;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.lock.LockManager;
import org.jboss.forge.furnace.repositories.AddonRepository;
import org.jboss.forge.furnace.repositories.AddonRepositoryMode;
import org.jboss.forge.furnace.spi.ContainerLifecycleListener;
import org.jboss.forge.furnace.spi.ListenerRegistration;
import org.jboss.forge.furnace.versions.Version;

/**
 * Operations for initializing, starting, interacting with, and stopping a {@link Furnace} container.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface Furnace
{
   /**
    * Start this {@link Furnace} instance in a new background {@link Thread}.
    */
   public Furnace startAsync();

   /**
    * Start this {@link Furnace} instance in a new background {@link Thread}, using the given {@link ClassLoader} to load
    * core implementation resources.
    */
   public Furnace startAsync(ClassLoader loader);

   /**
    * Start this {@link Furnace} instance and wait for completion.
    */
   public Furnace start();

   /**
    * Start this {@link Furnace} instance and wait for completion, using the given {@link ClassLoader} to load core
    * implementation resources.
    */
   public Furnace start(ClassLoader loader);

   /**
    * Stop this {@link Furnace} instance.
    */
   public Furnace stop();

   /**
    * When server mode is set to <code>true</code>, {@link Furnace} will run as a server process until explicitly stopped.
    * When server mode is false, {@link Furnace} will process all inputs and exit once addons have completed any requested
    * operations.
    */
   public Furnace setServerMode(boolean server);

   /**
    * Get the central {@link AddonRegistry} for this {@link Furnace} instance. Contains {@link Addon} registration and
    * service information.
    */
   public AddonRegistry getAddonRegistry();

   /**
    * Get an immutable {@link List} of configured {@link AddonRepository} instances.
    */
   public List<AddonRepository> getRepositories();

   /**
    * Add an {@link AddonRepository} to be scanned for deployed and enabled {@link Addon} instances. This method must
    * not be called once {@link Furnace} is started.
    */
   public AddonRepository addRepository(AddonRepositoryMode mode, File repository);

   /**
    * Get the current runtime API version of {@link Furnace}.
    */
   public Version getVersion();

   /**
    * Register a {@link ContainerLifecycleListener} instance. Returns a {@link ListenerRegistration} that can be used to
    * un-register the listener.
    */
   public ListenerRegistration<ContainerLifecycleListener> addContainerLifecycleListener(
            ContainerLifecycleListener listener);

   /**
    * Get the {@link ClassLoader} from which {@link Furnace} loaded its internal classes.
    */
   public ClassLoader getRuntimeClassLoader();

   /**
    * Get the {@link LockManager} associated with this {@link Furnace} instance
    */
   public LockManager getLockManager();

   /**
    * Get the current status of this {@link Furnace} container
    */
   public ContainerStatus getStatus();

   /**
    * Set the arguments with which {@link Furnace} should start. Typically this will simply be passed through from
    * <code>public static void main(String[] args)</code>.
    */
   public void setArgs(String[] args);

   /**
    * Get the arguments with which {@link Furnace} should start. Typically this will simply be passed through from
    * <code>public static void main(String[] args)</code>.
    * 
    * @return the arguments, or <code>null</code> if no arguments were set.
    */
   public String[] getArgs();

}