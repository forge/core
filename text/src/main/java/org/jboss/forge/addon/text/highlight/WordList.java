/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.text.highlight;

import java.util.HashMap;
import java.util.Map;

public class WordList<T>
{

   private T defaultValue;
   private Map<String, T> lists;
   private boolean caseInsensitive;

   public WordList(T defaultValue)
   {
      this(defaultValue, false);
   }

   public WordList(T defaultValue, boolean caseInsensitive)
   {
      this.defaultValue = defaultValue;
      this.caseInsensitive = caseInsensitive;
      this.lists = new HashMap<String, T>();
   }

   public WordList<T> add(String[] list, T type)
   {
      for (String word : list)
      {
         this.lists.put(caseInsensitive ? word.toLowerCase() : word, type);
      }
      return this;
   }

   public T lookup(String value)
   {
      T found = lists.get(caseInsensitive ? value.toLowerCase() : value);
      return found != null ? found : defaultValue;
   }

   public WordList<T> clone() {
      WordList<T> clone = new WordList<T>(this.defaultValue, this.caseInsensitive);
      clone.lists = new HashMap<String, T>();
      for(Map.Entry<String, T> entry : this.lists.entrySet()) {
         clone.lists.put(entry.getKey(), entry.getValue());
      }
      return clone;
   }
}
