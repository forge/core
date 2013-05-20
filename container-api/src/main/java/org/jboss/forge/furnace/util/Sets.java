package org.jboss.forge.furnace.util;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Utilities for interacting with {@link Set} instances.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class Sets
{
   /**
    * Get a new instance of a concurrent {@link Set} (implemented by {@link ConcurrentHashMap}).
    */
   public static <T> Set<T> getConcurrentSet(Class<T> type)
   {
      return Collections.newSetFromMap(new ConcurrentHashMap<T, Boolean>());
   }

   /**
    * Get a new instance of a concurrent {@link Set} (implemented by {@link ConcurrentHashMap}).
    */
   public static <T> Set<T> getConcurrentSet()
   {
      return Collections.newSetFromMap(new ConcurrentHashMap<T, Boolean>());
   }
}