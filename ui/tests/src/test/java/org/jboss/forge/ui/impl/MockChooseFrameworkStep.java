package org.jboss.forge.ui.impl;

import org.jboss.forge.ui.Result;
import org.jboss.forge.ui.UICommand;
import org.jboss.forge.ui.UICommandID;
import org.jboss.forge.ui.UIContext;
import org.jboss.forge.ui.UIValidationContext;
import org.jboss.forge.ui.base.SimpleUICommandID;

public class MockChooseFrameworkStep implements UICommand
{

   @Override
   public UICommandID getId()
   {
      return new SimpleUICommandID("Choose Framework", "Pick the framework you wish to use for this command.");
   }

   @Override
   public void initializeUI(UIContext context) throws Exception
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

}
