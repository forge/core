package org.jboss.forge.addon.text.highlight;

import java.util.HashMap;
import java.util.Map;

public class Options extends HashMap<String, Object> implements Map<String, Object>
{

   private static final long serialVersionUID = 1L;

   private Options()
   {
   }

   public static Options create()
   {
      return new Options();
   }

   public Options add(String key, Object value)
   {
      this.put(key, value);
      return this;
   }
}
