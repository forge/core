/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.ui.context;

import java.util.HashMap;
import java.util.Map;

/**
 * A default implementation of {@link UIContext}.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class UIContextBase implements UIContext
{
   private Map<Object, Object> map = new HashMap<Object, Object>();

   @Override
   public <T> UISelection<T> getInitialSelection()
   {
      return null;
   }

   @Override
   public Object getAttribute(Object key)
   {
      return map.get(key);
   }

   @Override
   public Object removeAttribute(Object key)
   {
      return map.remove(key);
   }

   @Override
   public void setAttribute(Object key, Object value)
   {
      map.put(key, value);
   }
}
