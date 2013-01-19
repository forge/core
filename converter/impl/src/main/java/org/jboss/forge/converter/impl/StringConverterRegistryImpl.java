/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.converter.impl;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;

import javax.enterprise.inject.Vetoed;

import org.jboss.forge.converter.StringConverter;
import org.jboss.forge.converter.StringConverterRegistry;

/**
 * The implementation for the {@link StringConverterRegistry}
 *
 * NOTE: Discuss about this class
 */
@Vetoed
public enum StringConverterRegistryImpl implements StringConverterRegistry
{
   INSTANCE;

   private Map<Class<?>, StringConverter<?>> values = Collections
            .synchronizedMap(new IdentityHashMap<Class<?>, StringConverter<?>>());

   @Override
   public <T> void addConverter(Class<T> type, StringConverter<T> converter)
   {
      values.put(type, converter);
   }

   @Override
   public void removeConverter(Class<?> type)
   {
      values.remove(type);
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T> StringConverter<T> getConverter(Class<T> type)
   {
      return (StringConverter<T>) values.get(type);
   }

}
