/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.database.tools.connections;

import org.jboss.forge.addon.ui.input.ValueChangeListener;
import org.jboss.forge.addon.ui.input.events.ValueChangeEvent;

/**
 * @author <a href="lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class CompositeValueChangeListener implements ValueChangeListener
{
   private ValueChangeListener[] listeners;

   public CompositeValueChangeListener(ValueChangeListener... listeners)
   {
      this.listeners = listeners;
   }

   @Override
   public void valueChanged(ValueChangeEvent event)
   {
      if (listeners != null)
      {
         for (ValueChangeListener listener : listeners)
         {
            if (listener != null)
            {
               listener.valueChanged(event);
            }
         }
      }
   }

}
