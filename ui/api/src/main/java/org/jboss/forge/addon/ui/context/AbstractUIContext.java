/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.jboss.forge.addon.ui.UIProvider;
import org.jboss.forge.addon.ui.controller.CommandExecutionListener;
import org.jboss.forge.furnace.spi.ListenerRegistration;

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
   private final Set<CommandExecutionListener> listeners = new LinkedHashSet<>();

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
   public Map<Object, Object> getAttributeMap()
   {
      return map;
   }
   
   @Override
   public UIProvider getProvider()
   {
      throw new UnsupportedOperationException("not implemented yet");
   }

   @Override
   public void close()
   {
      clearListeners();
   }

   @Override
   public ListenerRegistration<CommandExecutionListener> addCommandExecutionListener(
            final CommandExecutionListener listener)
   {
      listeners.add(listener);
      return new ListenerRegistration<CommandExecutionListener>()
      {
         @Override
         public CommandExecutionListener removeListener()
         {
            listeners.remove(listener);
            return listener;
         }
      };
   }

   public Set<CommandExecutionListener> getListeners()
   {
      return Collections.unmodifiableSet(listeners);
   }

   public void clearListeners()
   {
      listeners.clear();
   }

}
