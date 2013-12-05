/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.impl.controller;

import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.spi.UIContextFactory;
import org.jboss.forge.furnace.addons.AddonRegistry;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
class SingleCommandController extends AbstractCommandController
{
   public SingleCommandController(AddonRegistry addonRegistry, UIContextFactory contextFactory, UICommand initialCommand)
   {
      super(addonRegistry, contextFactory, initialCommand);
   }

   @Override
   public void initialize() throws Exception
   {
      UIBuilder uiBuilder = contextFactory.createUIBuilder(context);
      initialCommand.initializeUI(uiBuilder);
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
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public UIValidationContext validate()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public boolean isCurrentCommandRenderable()
   {
      return true;
   }

   @Override
   public void valueChanged()
   {
      // TODO Auto-generated method stub

   }

}
