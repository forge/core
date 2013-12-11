/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.jboss.forge.addon.ui.controller.CommandExecutionListener;
import org.jboss.forge.furnace.spi.ListenerRegistration;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public abstract class AbstractUIProvider implements UIProvider
{
   private final List<CommandExecutionListener> listeners = new LinkedList<CommandExecutionListener>();

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

   public List<CommandExecutionListener> getListeners()
   {
      return Collections.unmodifiableList(listeners);
   }

   public void clearListeners()
   {
      listeners.clear();
   }

}
