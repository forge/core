/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.impl.context;

import java.util.Map;
import java.util.Set;

import org.jboss.forge.addon.ui.UIProvider;
import org.jboss.forge.addon.ui.command.CommandExecutionListener;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.furnace.spi.ListenerRegistration;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class DelegatingUIContext implements UIContext
{
   private final UIContext context;
   private final UIProvider provider;

   public DelegatingUIContext(UIContext context, UIProvider provider)
   {
      super();
      this.context = context;
      this.provider = provider;
   }

   @Override
   public void close() throws Exception
   {
      context.close();
   }

   @Override
   public Map<Object, Object> getAttributeMap()
   {
      return context.getAttributeMap();
   }

   @Override
   public <SELECTIONTYPE> UISelection<SELECTIONTYPE> getInitialSelection()
   {
      return context.getInitialSelection();
   }

   @Override
   public <SELECTIONTYPE> void setSelection(SELECTIONTYPE resource)
   {
      context.setSelection(resource);
   }

   @Override
   public <SELECTIONTYPE> void setSelection(UISelection<SELECTIONTYPE> selection)
   {
      context.setSelection(selection);
   }

   @Override
   public <SELECTIONTYPE> UISelection<SELECTIONTYPE> getSelection()
   {
      return context.getSelection();
   }

   @Override
   public UIProvider getProvider()
   {
      return provider == null ? context.getProvider() : provider;
   }

   @Override
   public ListenerRegistration<CommandExecutionListener> addCommandExecutionListener(CommandExecutionListener listener)
   {
      return context.addCommandExecutionListener(listener);
   }

   @Override
   public Set<CommandExecutionListener> getListeners()
   {
      return context.getListeners();
   }

}
