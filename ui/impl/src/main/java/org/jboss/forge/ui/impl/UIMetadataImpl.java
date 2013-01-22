/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.ui.impl;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.jboss.forge.ui.UIMetadata;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class UIMetadataImpl implements UIMetadata
{
   private final Map<Object, Object> store = new ConcurrentHashMap<Object, Object>();

   @Override
   public Object get(Object key)
   {
      return store.get(key);
   }

   @Override
   public UIMetadata set(Object key, Object value)
   {
      store.put(key, value);
      return this;
   }

   @Override
   public Iterator<Entry<Object, Object>> iterator()
   {
      return store.entrySet().iterator();
   }

}
