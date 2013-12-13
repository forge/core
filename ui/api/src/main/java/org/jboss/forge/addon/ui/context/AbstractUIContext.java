/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.context;

import java.util.HashMap;
import java.util.Map;

import org.jboss.forge.addon.ui.UIProvider;

/**
 * This class provides a skeletal implementation of the <tt>UIContext</tt> interface, to minimize the effort required to
 * implement this interface.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public abstract class AbstractUIContext implements UIContext
{
   private final Map<Object, Object> map = new HashMap<>();
   private Object selection;

   @Override
   @SuppressWarnings("unchecked")
   public <SELECTIONTYPE> SELECTIONTYPE getSelection()
   {
      return (SELECTIONTYPE) selection;
   }

   @Override
   public <SELECTIONTYPE> void setSelection(SELECTIONTYPE selection)
   {
      this.selection = selection;
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

   @Override
   public UIProvider getProvider()
   {
      throw new UnsupportedOperationException("not implemented yet");
   }

   @Override
   public void close() throws Exception
   {
   }
}
