/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.controller;

import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.result.Result;

/**
 * A Controller for a specific {@link UICommand}
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public interface CommandController
{
   /**
    * Is the wizard allowed to execute ?
    */
   public boolean canExecute();

   /**
    * Finish clicked
    * 
    * @throws Exception if anything wrong happens
    */
   public Result execute() throws Exception;

   /**
    * Call validate for the current page + inputs
    */
   public UIValidationContext validate();

   /**
    * Returns true if the current command has any input to render.
    */
   public boolean isCurrentCommandRenderable();

   /**
    * Lets the controller know that some value in the Wizard was modified, so it should invalidate subsequent pages.
    */
   public void valueChanged();
}