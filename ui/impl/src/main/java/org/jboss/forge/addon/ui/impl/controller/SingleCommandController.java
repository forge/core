/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.impl.controller;

import java.util.ArrayList;
import java.util.List;

import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.impl.context.UIBuilderImpl;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.spi.UIContextFactory;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.util.Assert;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
class SingleCommandController extends AbstractCommandController
{
   private UIBuilderImpl uiBuilder;

   public SingleCommandController(AddonRegistry addonRegistry, UIContextFactory contextFactory, UICommand initialCommand)
            throws Exception
   {
      super(addonRegistry, contextFactory, initialCommand);
   }

   @Override
   public void initialize() throws Exception
   {
      if (!isInitialized())
      {
         uiBuilder = new UIBuilderImpl(context);
         initialCommand.initializeUI(uiBuilder);
      }
   }

   protected boolean isInitialized()
   {
      return (uiBuilder != null);
   }

   protected void assertInitialized()
   {
      Assert.isTrue(isInitialized(), "Controller must be initialized.");
   }

   @Override
   public boolean canExecute()
   {
      return false;
   }

   @Override
   public Result execute() throws Exception
   {
      assertInitialized();
      return null;
   }

   @Override
   public List<InputComponent<?, Object>> getInputs()
   {
      assertInitialized();
      return new ArrayList<InputComponent<?, Object>>(uiBuilder.getInputs().values());
   }

   @Override
   public CommandController setValueFor(String inputName, Object value)
   {
      assertInitialized();
      return this;
   }

   @Override
   public Object getValueFor(String inputName)
   {
      assertInitialized();
      return null;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.jboss.forge.addon.ui.controller.CommandController#getMetadata()
    */
   @Override
   public UICommandMetadata getMetadata()
   {
      return null;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.jboss.forge.addon.ui.controller.CommandController#isEnabled()
    */
   @Override
   public boolean isEnabled()
   {
      return false;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.jboss.forge.addon.ui.controller.CommandController#getInput(java.lang.String)
    */
   @Override
   public InputComponent<?, Object> getInput(String inputName)
   {
      assertInitialized();
      return null;
   }
}
