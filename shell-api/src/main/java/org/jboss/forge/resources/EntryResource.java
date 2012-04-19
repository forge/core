/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
