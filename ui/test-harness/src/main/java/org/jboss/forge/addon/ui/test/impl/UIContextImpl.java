/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.test.impl;

import java.util.Collections;

import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.ui.UIProvider;
import org.jboss.forge.addon.ui.context.AbstractUIContext;
import org.jboss.forge.addon.ui.context.UIContextListener;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.util.Selections;
import org.jboss.forge.furnace.util.Assert;

public class UIContextImpl extends AbstractUIContext
{
   private UISelection<?> initialSelection;
   private final Iterable<UIContextListener> listeners;
   private final UIProvider provider;

   public UIContextImpl(boolean gui, UISelection<?> selection)
   {
      this.provider = new UIProviderImpl(gui);
      this.listeners = Collections.emptyList();
      this.initialSelection = selection;
      init();
   }

   public UIContextImpl(UIProvider provider, Iterable<UIContextListener> listeners,
            UISelection<Resource<?>> initialSelection)
   {
      Assert.notNull(provider, "Provider must not be null");
      this.provider = provider;
      this.listeners = listeners;
      this.initialSelection = initialSelection;
      init();
   }

   @SuppressWarnings("unchecked")
   public <T> void setInitialSelection(T... selection)
   {
      this.initialSelection = Selections.from(selection);
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T> UISelection<T> getInitialSelection()
   {
      return (UISelection<T>) initialSelection;
   }

   public void init()
   {
      for (UIContextListener listener : listeners)
      {
         listener.contextInitialized(this);
      }
   }

   @Override
   public void close()
   {
      for (UIContextListener listener : listeners)
      {
         listener.contextDestroyed(this);
      }
   }

   @Override
   public UIProvider getProvider()
   {
      return provider;
   }
}
