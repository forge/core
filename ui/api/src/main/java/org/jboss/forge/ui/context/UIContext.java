/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui.context;


public interface UIContext
{
   /**
    * Get an {@link Object} from the {@link UIContext} attribute map.
    * 
    * @return <code>null</code> if no value was set.
    */
   public Object getAttribute(Object key);

   /**
    * Remove an {@link Object} from the {@link UIContext} attribute map.
    * 
    * @return <code>null</code> if no value was set.
    */
   public Object removeAttribute(Object key);

   /**
    * Set an {@link Object} key in the {@link UIContext} attribute map to the given value.
    */
   public void setAttribute(Object key, Object value);

   /**
    * Get the user's initial selection.
    */
   <SELECTIONTYPE> UISelection<SELECTIONTYPE> getInitialSelection();
}
