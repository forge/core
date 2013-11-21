/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui;

import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;

/**
 * An {@link UICommand} represents a possible interaction from the user with the installed addon.
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public interface UICommand extends UIValidator
{
   /**
    * @param context the {@link UIContext} provided by the UI implementation, never null
    * @return The Metadata of this command. Should never return null
    */
   public UICommandMetadata getMetadata(UIContext context);

   /**
    * Called before initializing the UI to check if it's available for execution
    * 
    * @param context the {@link UIContext} provided by the UI implementation, never null
    * @return true if this command is available for execution
    */
   public boolean isEnabled(UIContext context);

   /**
    * Called before rendering the UI. Should add the {@link InputComponent} objects to be displayed in the provided
    * {@link UIBuilder}
    * 
    * @param builder The builder on which the UI should be built upon. Never null
    * @throws Exception if anything wrong happens
    */
   public void initializeUI(UIBuilder builder) throws Exception;

   /**
    * Called when the {@link UICommand} should be executed.
    * 
    * This method should NEVER execute if {@link UICommand#isEnabled(UIContext)} returns false and
    * {@link UICommand#validate(UIValidationContext)} contains validation errors
    * 
    * @param context the context of the interaction
    * @return the result of this execution (see the {@link Results} class)
    * @throws Exception if anything wrong happens
    */
   public Result execute(UIExecutionContext context) throws Exception;
}
