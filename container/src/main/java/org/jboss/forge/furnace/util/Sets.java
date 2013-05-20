package org.jboss.forge.furnace.util;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class Sets
{
   public static <T> Set<T> getConcurrentSet(Class<T> type)
   {
      return Collections.newSetFromMap(new ConcurrentHashMap<T, Boolean>());
   }

   public static <T> Set<T> getConcurrentSet()
   {
      return Collections.newSetFromMap(new ConcurrentHashMap<T, Boolean>());
   }
}