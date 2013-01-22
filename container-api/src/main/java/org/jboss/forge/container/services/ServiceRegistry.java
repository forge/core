/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.container.services;

import java.util.Set;

/**
 * Contains the collection of all installed and available {@link ExportedInstance} types.
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface ServiceRegistry
{
   <T> ExportedInstance<T> getExportedInstance(Class<T> clazz);

   <T> ExportedInstance<T> getExportedInstance(String clazz);

   <T> Set<ExportedInstance<T>> getExportedInstances(Class<T> clazz);

   <T> Set<ExportedInstance<T>> getExportedInstances(String clazz);

   Set<Class<?>> getServices();

   boolean hasService(Class<?> clazz);

   boolean hasService(String clazz);

}
