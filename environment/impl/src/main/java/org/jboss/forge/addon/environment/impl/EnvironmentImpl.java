/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.environment.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jboss.forge.addon.environment.Category;
import org.jboss.forge.addon.environment.Environment;

public class EnvironmentImpl implements Environment
{
   private static final Map<String, Map<Object, Object>> CATEGORIZED_MAP = Collections.synchronizedMap(
            new HashMap<String, Map<Object, Object>>());

   @SuppressWarnings("unchecked")
   @Override
   public <K, V> Map<K, V> get(Class<? extends Category> key)
   {
      Map<Object, Object> map = CATEGORIZED_MAP.get(key.getName());
      if (map == null)
      {
         map = new ConcurrentHashMap<Object, Object>();
         CATEGORIZED_MAP.put(key.getName(), map);
      }
      return (Map<K, V>) map;
   }

}
