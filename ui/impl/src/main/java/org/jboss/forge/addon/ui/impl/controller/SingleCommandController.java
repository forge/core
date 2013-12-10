/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.impl.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.impl.context.UIBuilderImpl;
import org.jboss.forge.addon.ui.impl.context.UIValidationContextImpl;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.spi.UIContextFactory;
import org.jboss.forge.furnace.addons.AddonRegistry;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
class SingleCommandController extends AbstractCommandController
{
   private Map<String, InputComponent<?, Object>> inputs = new LinkedHashMap<String, InputComponent<?, Object>>();

   public SingleCommandController(AddonRegistry addonRegistry, UIContextFactory contextFactory, UICommand initialCommand)
            throws Exception
   {
      super(addonRegistry, contextFactory, initialCommand);
      initialize();
   }

   private void initialize() throws Exception
   {
      UIBuilderImpl uiBuilder = new UIBuilderImpl(context);
      initialCommand.initializeUI(uiBuilder);
      inputs = uiBuilder.getInputs();
   }

   @Override
   public boolean canExecute()
   {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public Result execute() throws Exception
   {

      return null;
   }

   @Override
   public UIValidationContextImpl validate()
   {
      UIValidationContextImpl validationContext = new UIValidationContextImpl(context);
      initialCommand.validate(validationContext);
      for (InputComponent<?, ?> input : inputs.values())
      {
         validationContext.setCurrentInputComponent(input);
         input.validate(validationContext);
      }
      validationContext.setCurrentInputComponent(null);
      return validationContext;
   }

   @Override
   public List<InputComponent<?, Object>> getInputs()
   {
      return new ArrayList<InputComponent<?, Object>>(inputs.values());
   }

   @Override
   public CommandController setValueFor(String inputName, Object value)
   {
      return this;
   }

   @Override
   public Object getValueFor(String inputName)
   {
      return null;
   }
}
