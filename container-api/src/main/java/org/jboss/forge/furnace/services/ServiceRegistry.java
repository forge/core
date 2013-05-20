/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.furnace.services;

import java.util.Collections;
import java.util.Set;

/**
 * Contains the collection of all installed and available {@link ExportedInstance} types.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface ServiceRegistry
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
    * Return <code>true</code> if the given {@link Class} type is registered as an {@link Exported} service, otherwise
    * return <code>false</code>.
    */
   boolean hasService(Class<?> clazz);

   /**
    * Return <code>true</code> if a type with the given name is registered as an {@link Exported} service, otherwise
    * return <code>false</code>.
    */
   boolean hasService(String clazz);

}
