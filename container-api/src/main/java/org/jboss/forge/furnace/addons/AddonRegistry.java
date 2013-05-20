/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.furnace.addons;

import java.util.Collections;
import java.util.Set;

import org.jboss.forge.furnace.services.Exported;
import org.jboss.forge.furnace.services.ExportedInstance;

/**
 * Provides methods for registering, starting, stopping, and interacting with registered {@link Addon} instances.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface AddonRegistry
{
   /**
    * Get the set of currently available {@link Exported} services of the given {@link Class} type. Return
    * {@link Collections#EMPTY_SET} if no matching services are found.
    * 
    * @return the {@link Set} of {@link ExportedInstance} objects (Never null.)
    */
   <T> Set<ExportedInstance<T>> getExportedInstances(Class<T> clazz);

   /**
    * Get the set of currently available {@link Exported} services types with {@link Class#getName()} matching the given
    * name. Return {@link Collections#EMPTY_SET} if no matching services are found.
    * 
    * @return the {@link Set} of {@link ExportedInstance} objects (Never null.)
    */
   <T> Set<ExportedInstance<T>> getExportedInstances(String clazz);

   /**
    * Get an instance of any currently available {@link Exported} service of the given {@link Class} type. Return
    * <code>null</code> if no matching service can be found.
    * 
    * @return the {@link ExportedInstance} (May be null.)
    */
   <T> ExportedInstance<T> getExportedInstance(Class<T> type);

   /**
    * Get an instance of any currently available {@link Exported} service types with {@link Class#getName()} matching
    * the given name. Return <code>null</code> if no matching service can be found.
    * 
    * @return the {@link ExportedInstance} (May be null.)
    */
   <T> ExportedInstance<T> getExportedInstance(String type);

   /**
    * Get a {@link Set} of all currently available {@link Exported} service types.
    * 
    * @return the {@link Set} of {@link Class} types (Never null.)
    */
   Set<Class<?>> getExportedTypes();

   /**
    * Get a {@link Set} of currently available {@link Exported} service types for which
    * {@link Class#isAssignableFrom(Class)} returns <code>true</code>.
    * 
    * @return the {@link Set} of {@link Class} types (Never null.)
    */
   <T> Set<Class<T>> getExportedTypes(Class<T> type);

   /**
    * Get the registered {@link Addon} for the given {@link AddonId} instance. If no such {@link Addon} is currently
    * registered, register it and return the new reference.
    * 
    * @return the registered {@link Addon} (Never null.)
    */
   Addon getAddon(AddonId id);

   /**
    * Get all currently registered {@link Addon} instances.
    * 
    * @return the {@link Set} of {@link Addon} instances. (Never null.)
    */
   Set<Addon> getAddons();

   /**
    * Get all registered {@link Addon} instances matching the given {@link AddonFilter}.
    * 
    * @return the {@link Set} of {@link Addon} instances. (Never null.)
    */
   Set<Addon> getAddons(AddonFilter filter);
}
