/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.container.services;

import java.util.Set;

/**
 * Contains the collection of all installed and available {@link RemoteInstance} types.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface ServiceRegistry
{
   <T> void addService(Class<T> clazz);

   <T> RemoteInstance<T> getRemoteInstance(Class<T> clazz);

   <T> RemoteInstance<T> getRemoteInstance(String clazz);

   <T> Set<RemoteInstance<T>> getRemoteInstances(Class<T> clazz);

   <T> Set<RemoteInstance<T>> getRemoteInstances(String clazz);

   Set<Class<?>> getServices();

   boolean hasService(Class<?> clazz);

}
