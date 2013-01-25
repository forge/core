/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.environment.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Singleton;

import org.jboss.forge.container.services.Exported;
import org.jboss.forge.environment.Category;
import org.jboss.forge.environment.Environment;

@Exported
@Singleton
public class EnvironmentImpl implements Environment
{
   private Map<String, Map<Object, Object>> categorizedMap =
            Collections.synchronizedMap(
                     new HashMap<String, Map<Object, Object>>());

   @SuppressWarnings("unchecked")
   @Override
   public <K, V> Map<K, V> get(Class<? extends Category> key)
   {
      Map<Object, Object> map = categorizedMap.get(key.getName());
      if (map == null)
      {
         map = new ConcurrentHashMap<Object, Object>();
         categorizedMap.put(key.getName(), map);
      }
      return (Map<K, V>) map;
   }

}
