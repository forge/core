/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.util;

import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UINavigationContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.wizard.UIWizard;

/**
 * @author Vineet Reynolds
 */
public class MockWizard implements UIWizard
{

   @Override
   public NavigationResult next(UINavigationContext context) throws Exception
   {
      return null;
   }

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(MockWizard.class);
   }

   @Override
   public boolean isEnabled(UIContext context)
   {
      return false;
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {

   }

   @Override
   public void validate(UIValidationContext context)
   {

   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      return null;
   }
}
