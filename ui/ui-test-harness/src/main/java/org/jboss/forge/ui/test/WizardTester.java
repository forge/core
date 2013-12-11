/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui.test;

import java.util.List;

import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.ui.CommandExecutionListener;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.wizard.UIWizard;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;

/**
 * A {@link WizardTester} allows easy testing of Wizard flows by using the Forge UI API
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 * @param <W>
 */
public interface WizardTester<W extends UIWizard>
{

   /**
    * Sets the initial selection for the wizard
    * 
    * @param the selected Resource
    */
   public void setInitialSelection(Resource<?>... selection);

   /**
    * Launch the Wizard
    */
   public void launch() throws Exception;

   /**
    * Next Wizard Page
    * 
    * @throws Exception this is the last page or there are validation errors
    */
   public abstract void next() throws Exception;

   /**
    * Previous Wizard Page
    * 
    * @throws Exception this is the last page
    */
   public abstract void previous() throws Exception;

   /**
    * Is it possible to navigate to the next page ?
    * 
    * @throws Exception this is the first page or there are validation errors
    */
   public boolean canFlipToNextPage();

   /**
    * Is it possible to navigate to the previous page ?
    * 
    * @throws Exception this is the first page or there are validation errors
    */
   public boolean canFlipToPreviousPage();

   /**
    * Is the current wizard page valid ?
    * 
    * @return true if valid, false otherwise
    */
   public boolean isValid();

   /**
    * The validation errors for the current page
    */
   public List<String> getValidationErrors();

   /**
    * Is the wizard allowed to finish?
    */
   public boolean canFinish();

   /**
    * Finish clicked
    * 
    * @return A list of results, in order, from the executed {@link UIWizardStep} instances.
    * @throws Exception if anything wrong happens
    */
   public List<Result> finish() throws Exception;

   /**
    * Finish clicked
    * 
    * @param listener if you wish to listen for the result for each page.
    * @return A list of results, in order, from the executed {@link UIWizardStep} instances.
    * @throws Exception if anything wrong happens
    */
   public List<Result> finish(CommandExecutionListener listener) throws Exception;

   /**
    * Sets the value of a property
    * 
    * TODO: Property should be typesafe.
    */
   public void setValueFor(String property, Object value);

   /**
    * Returns the {@link InputComponent} by the name
    */
   public InputComponent<?, ?> getInputComponent(String property);

   /**
    * Is this current wizard enabled ?
    */
   public boolean isEnabled();
}
