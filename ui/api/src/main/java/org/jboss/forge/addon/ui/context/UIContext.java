/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.context;

import java.util.Map;
import java.util.Set;

import org.jboss.forge.addon.ui.UIProvider;
import org.jboss.forge.addon.ui.command.CommandExecutionListener;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.wizard.UIWizard;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;
import org.jboss.forge.furnace.spi.ListenerRegistration;

/**
 * An {@link UIContext} holds information about the environment the {@link UICommand} is being run, such as the initial
 * selection and attributes that could be shared among several {@link UIWizardStep} of a single {@link UIWizard}
 * instance.
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 *
 */
public interface UIContext extends AutoCloseable
{
   /**
    * Returns a modifiable map for this {@link UIContext}
    */
   Map<Object, Object> getAttributeMap();

   /**
    * Get the user's {@link UISelection} at the time this command was initialized/selected. (Never <code>null</code>.)
    */
   <SELECTIONTYPE> UISelection<SELECTIONTYPE> getInitialSelection();

   /**
    * Sets a new selection. (This will change the current working directory or {@link UISelection} - behavior varies
    * somewhat depending on the {@link UIProvider} implementation.) The user's selection will be not be changed until
    * after {@link UICommand#execute(UIExecutionContext)} has completed.
    *
    * @see UISelection
    */
   <SELECTIONTYPE> void setSelection(SELECTIONTYPE resource);

   /**
    * Sets a new selection. (This will change the current working directory or {@link UISelection} - behavior varies
    * somewhat depending on the {@link UIProvider} implementation.) The user's selection will be not be changed until
    * after {@link UICommand#execute(UIExecutionContext)} has completed.
    *
    * @see UISelection
    */
   <SELECTIONTYPE> void setSelection(UISelection<SELECTIONTYPE> selection);

   /**
    * Returns the selection set by {@link UIContext#setSelection(Object)} or {@link UIContext#setSelection(UISelection)}
    * , or if no selection was set for this interaction, the value of {@link #getInitialSelection()}.
    * <p/>
    * (<b>***WARNING***</b> - This is <b>NOT</b> the same as {@link #getInitialSelection()}. It will <b>NOT</b> return
    * the user's initial selection if a new selection has previously been set via {@link #setSelection(Object)} or
    * {@link #setSelection(UISelection)}.)
    *
    * @see UISelection
    * @see #getInitialSelection()
    */
   <SELECTIONTYPE> UISelection<SELECTIONTYPE> getSelection();

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