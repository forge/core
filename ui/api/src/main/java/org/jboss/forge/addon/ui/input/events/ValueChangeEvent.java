/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.input.events;

import java.util.EventObject;

import org.jboss.forge.addon.ui.input.InputComponent;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ValueChangeEvent extends EventObject
{
   private static final long serialVersionUID = 1L;

   private final Object oldValue;
   private final Object newValue;

   public ValueChangeEvent(InputComponent<?, ?> source, Object oldValue, Object newValue)
   {
      super(source);
      this.oldValue = oldValue;
      this.newValue = newValue;
   }

   @Override
   public InputComponent<?, ?> getSource()
   {
      return (InputComponent<?, ?>) super.getSource();
   }

   /**
    * @return the oldValue
    */
   public Object getOldValue()
   {
      return oldValue;
   }

   /**
    * @return the newValue
    */
   public Object getNewValue()
   {
      return newValue;
   }

}