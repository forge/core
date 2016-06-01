/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
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
