/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.context;

import java.util.Map;
import java.util.Set;

import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.UIProvider;
import org.jboss.forge.addon.ui.controller.CommandExecutionListener;
import org.jboss.forge.addon.ui.wizard.UIWizard;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;
import org.jboss.forge.furnace.spi.ListenerRegistration;

/**
 * An {@link UIContext} holds information about the environment the {@link UICommand} is being run, such as the initial
 * selection and attributes that could be shared among several {@link UIWizardStep} of a single {@link UIWizard}
 * instance.
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public interface UIContext extends AutoCloseable
{
   /**
    * Returns a modifiable map for this {@link UIContext}
    */
   Map<Object, Object> getAttributeMap();

   /**
    * Get the user's selection at the time this command was initialized/selected. (Never <code>null</code>.)
    */
   <SELECTIONTYPE> UISelection<SELECTIONTYPE> getInitialSelection();

   /**
    * Sets the current selection. (This will change the current working directory or current selection, depending on the
    * {@link UIProvider} implementation.)
    */
   <SELECTIONTYPE> void setSelection(SELECTIONTYPE resource);

   /**
    * Returns the selection set by {@link UIContext#setSelection(Object)}, or <code>null</code> if no selection was
    * provided for this interaction.
    */
   <SELECTIONTYPE> SELECTIONTYPE getSelection();

   /**
    * Returns the {@link UIProvider} that created this {@link UIContext} object. (Never <code>null</code>.)
    */
   UIProvider getProvider();

   /**
    * Add a {@link CommandExecutionListener}, returning the {@link ListenerRegistration} with which it may subsequently
    * be removed.
    */
   ListenerRegistration<CommandExecutionListener> addCommandExecutionListener(CommandExecutionListener listener);

   /**
    * Returns the registered {@link CommandExecutionListener} objects for this {@link UIContext}. (Never
    * <code>null</code>.)
    */
   Set<CommandExecutionListener> getListeners();

}