/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.input.events;

import java.util.EventObject;

import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UIInputMany;
import org.jboss.forge.addon.ui.input.UISelectMany;
import org.jboss.forge.addon.ui.input.UISelectOne;

/**
 * An event object fired when the value of a {@link InputComponent} changes
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ValueChangeEvent extends EventObject
{
   private static final long serialVersionUID = 1L;

   private final Object oldValue;
   private final Object newValue;
   private final int[] newSelectedIndexes;
   private final int[] oldSelectedIndexes;

   public ValueChangeEvent(InputComponent<?, ?> source, Object oldValue, Object newValue)
   {
      super(source);
      this.oldValue = oldValue;
      this.newValue = newValue;
      this.newSelectedIndexes = this.oldSelectedIndexes = new int[0];
   }

   public ValueChangeEvent(InputComponent<?, ?> source, Object oldValue, Object newValue, int[] oldSelectedIndexes,
            int[] newSelectedIndexes)
   {
      super(source);
      this.oldValue = oldValue;
      this.newValue = newValue;
      this.oldSelectedIndexes = oldSelectedIndexes;
      this.newSelectedIndexes = newSelectedIndexes;
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

   /**
    * @return the previous selected indexes if the {@link #getSource()} is a {@link UISelectMany}. {@link UISelectOne}
    *         will always return an array with length = 1. Returns Returns an array of length zero if
    *         {@link #getSource()} is a {@link UIInput} or {@link UIInputMany}
    */
   public int[] getOldSelectedIndexes()
   {
      return oldSelectedIndexes;
   }

   /**
    * @return the new selected indexes if the {@link #getSource()} is a {@link UISelectMany}. {@link UISelectOne} will
    *         always return an array with length = 1. Returns an array of length zero if {@link #getSource()} is a
    *         {@link UIInput} or {@link UIInputMany}
    */
   public int[] getNewSelectedIndexes()
   {
      return newSelectedIndexes;
   }
}