/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.container.addons;

import java.util.Set;
import java.util.concurrent.Future;

import org.jboss.forge.container.services.ExportedInstance;

/**
 * Provides methods for registering, starting, stopping, and interacting with registered {@link Addon} instances.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public interface AddonRegistry
{
   boolean isRegistered(AddonId id);

   Addon getRegisteredAddon(AddonId id);

   /**
    * Get all currently registered {@link Addon} instances.
    */
   Set<Addon> getRegisteredAddons();

   /**
    * Get all registered {@link Addon} instances matching the given {@link AddonFilter}.
    */
   Set<Addon> getRegisteredAddons(AddonFilter filter);

   <T> Set<ExportedInstance<T>> getExportedInstances(Class<T> clazz);

   <T> Set<ExportedInstance<T>> getExportedInstances(String clazz);

   <T> ExportedInstance<T> getExportedInstance(Class<T> type);

   <T> ExportedInstance<T> getExportedInstance(String type);

   /**
    * Start the given {@link AddonId} and all its dependencies, if possible. Return a {@link Future} which can be used
    * to retrieve the final {@link Addon} after the operation has been performed. Existing dependents for which this
    * {@link AddonId} is an optional dependency will be restarted.
    */
   Future<Addon> start(AddonId addon);

   /**
    * Stop the given {@link Addon} that originated from this {@link AddonRegistry}. Also stop all dependent
    * {@link Addon} instances. Dependents for which this {@link Addon} is an optional dependency will be restarted.
    */
   void stop(Addon addon);
}
