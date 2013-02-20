package org.jboss.forge.ui.impl;

import org.jboss.forge.ui.UIBuilder;
import org.jboss.forge.ui.UICommandMetadata;
import org.jboss.forge.ui.base.UICommandMetadataBase;
import org.jboss.forge.ui.context.UIContext;
import org.jboss.forge.ui.context.UIValidationContext;
import org.jboss.forge.ui.result.NavigationResult;
import org.jboss.forge.ui.result.Result;
import org.jboss.forge.ui.wizard.UIWizardStep;

public class MockChooseFrameworkStep implements UIWizardStep
{

   @Override
   public boolean isEnabled(UIContext context)
   {
      return true;
   }

   @Override
   public UICommandMetadata getMetadata()
   {
      return new UICommandMetadataBase("Choose Framework", "Pick the framework you wish to use for this command.");
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
   public Result execute(UIContext context) throws Exception
   {
      return null;
   }

   @Override
   public NavigationResult next(UIContext context) throws Exception
   {
      return null;
   }
}
