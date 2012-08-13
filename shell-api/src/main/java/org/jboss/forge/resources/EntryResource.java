/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.resources;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Represents a Key-value entry
 * 
 * @author <a href="mailto:gegastaldi@gmail.com">George Gastaldi</a>
 * 
 */
public class EntryResource<K, V> extends VirtualResource<Entry<K, V>>
{

   private Entry<K, V> entry;

   public EntryResource(final Resource<?> parent, K key, V value)
   {
      super(parent);
      entry = new ConcreteEntry(key, value);
   }

   public K getKey()
   {
      return entry.getKey();
   }

   public V getValue()
   {
      return entry.getValue();
   }

   @Override
   public boolean delete() throws UnsupportedOperationException
   {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean delete(boolean recursive) throws UnsupportedOperationException
   {
      throw new UnsupportedOperationException();
   }

   @Override
   public String getName()
   {
      return getKey() + " = " + getValue();
   }

   @Override
   public Entry<K, V> getUnderlyingResourceObject()
   {
      return entry;
   }

   @Override
   protected List<Resource<?>> doListResources()
   {
      return Collections.emptyList();
   }

   /**
    * Implementation of {@link Entry} used in {@link EntryResource#getUnderlyingResourceObject()}
    * 
    * @author george
    * 
    */
   private class ConcreteEntry implements Map.Entry<K, V>
   {
      private K key;
      private V value;

      private ConcreteEntry(K key, V value)
      {
         super();
         this.key = key;
         this.value = value;
      }

      @Override
      public K getKey()
      {
         return key;
      }

      @Override
      public V getValue()
      {
         return value;
      }

      @Override
      public V setValue(V value)
      {
         this.value = value;
         return getValue();
      }

   }

}
