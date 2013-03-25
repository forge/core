/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addons;

import javax.inject.Inject;

import org.jboss.forge.ui.context.UIBuilder;
import org.jboss.forge.ui.context.UIContext;
import org.jboss.forge.ui.context.UIValidationContext;
import org.jboss.forge.ui.input.UIInput;
import org.jboss.forge.ui.metadata.UICommandMetadata;
import org.jboss.forge.ui.result.NavigationResult;
import org.jboss.forge.ui.result.Result;
import org.jboss.forge.ui.util.Categories;
import org.jboss.forge.ui.util.Metadata;
import org.jboss.forge.ui.wizard.UIWizardStep;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 *
 */
public class ForgeAddonSetupStep implements UIWizardStep
{
   @Inject
   private UIInput<Boolean> splitApiImpl;

   @Override
   public UICommandMetadata getMetadata()
   {
      return Metadata.forCommand(getClass()).name("Forge Addon Setup")
               .description("Enable Forge Addon development in your project.")
               .category(Categories.create("Project", "Forge"));
   }

   @Override
   public boolean isEnabled(UIContext context)
   {
      return true;
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      splitApiImpl.setLabel("Split API and Implementation projects");
      builder.add(splitApiImpl);
   }

   @Override
   public void validate(UIValidationContext validator)
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
