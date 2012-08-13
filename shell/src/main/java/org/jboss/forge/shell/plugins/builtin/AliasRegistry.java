/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.plugins.builtin;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Singleton
public class AliasRegistry
{
   private final Map<String, String> map = new HashMap<String, String>();

   public boolean hasAlias(final String alias)
   {
      return map.containsKey(alias);
   }

   public void createAlias(final String alias, final String command)
   {
      map.put(alias, command);
   }

   public void removeAlias(final String alias)
   {
      map.remove(alias);
   }

   public Map<String, String> getAliases()
   {
      return Collections.unmodifiableMap(map);
   }

   public String getAlias(final String alias)
   {
      return map.get(alias);
   }

}
