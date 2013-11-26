/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.controller;

import java.util.List;

import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.result.Result;

/**
 * A Controller for a specific {@link UICommand}
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public interface CommandController
{
   /**
    * Returns the initial {@link UICommand}
    */
   public UICommand getInitialCommand();

   /**
    * Launch the currrent wizard execution
    * 
    * @throws Exception
    */
   public void launch() throws Exception;

   /**
    * Is the wizard allowed to finish?
    */
   public boolean canFinish();

   /**
    * Lets the controller know that some value in the Wizard was modified, so it should invalidate subsequent pages.
    */
   public void valueChanged();

   /**
    * Finish clicked
    * 
    * @throws Exception if anything wrong happens
    */
   public Result finish() throws Exception;

   /**
    * Is the current wizard page valid ?
    * 
    * @return true if valid, false otherwise
    */
   public boolean isValid();

   /**
    * The validation errors for the current page
    */
   public List<String> getErrorMessages();
}
