/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.context;

import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.wizard.UIWizard;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;

/**
 * An {@link UIContext} holds information about the environment the {@link UICommand} is being run, such as the initial
 * selection and attributes that could be shared among several {@link UIWizardStep} of a single {@link UIWizard}
 * instance
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
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
    * Get the user's initial selection. Never null
    */
   <SELECTIONTYPE> UISelection<SELECTIONTYPE> getInitialSelection();

   /**
    * Sets the current selection.
    *
    * @param resource the resource to be selected
    */
   <SELECTIONTYPE> void setSelection(SELECTIONTYPE resource);

   /**
    * Returns the selection passed on the {@link UIContext#setSelection(Object)} method or null if no selection is
    * needed for this interaction
    *
    */
   <SELECTIONTYPE> SELECTIONTYPE getSelection();
}