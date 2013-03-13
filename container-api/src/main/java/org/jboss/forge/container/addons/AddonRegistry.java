/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.container.addons;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import org.jboss.forge.container.lock.LockManager;
import org.jboss.forge.container.services.ExportedInstance;
import org.jboss.forge.container.services.ServiceRegistry;

/**
 * Provides methods for registering, starting, stopping, and interacting with registered {@link Addon} instances.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public interface AddonRegistry extends LockManager
{
   Addon getRegisteredAddon(AddonId id);

   Set<Addon> getRegisteredAddons();

   Set<Addon> getRegisteredAddons(AddonFilter filter);

   boolean isRegistered(AddonId id);

   Map<Addon, ServiceRegistry> getServiceRegistries();

   <T> Set<ExportedInstance<T>> getExportedInstances(Class<T> clazz);

   <T> Set<ExportedInstance<T>> getExportedInstances(String clazz);

   <T> ExportedInstance<T> getExportedInstance(Class<T> type);

   <T> ExportedInstance<T> getExportedInstance(String type);

   /**
    * Start the given {@link Addon} and all its dependencies, if possible. Return a {@link Future} which can be used to
    * retrieve the final {@link Addon} {@link Status} after the operation has been performed.
    */
   Future<Status> start(Addon addon);

   /**
    * Start the given {@link Addon}, and all dependent {@link Addon} instances.
    */
   void stop(Addon addon);
}
