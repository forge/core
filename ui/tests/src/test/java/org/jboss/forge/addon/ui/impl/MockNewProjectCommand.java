package org.jboss.forge.addon.ui.impl;

import java.io.File;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;

public class MockNewProjectCommand implements UICommand, UIWizardStep
{

   @Override
   public boolean isEnabled(UIContext context)
   {
      return true;
   }

   @Inject
   private UIInput<String> projectName;

   @Inject
   private UIInput<File> projectDir;

   // Rendered as a checkbox
   @Inject
   private UIInput<Boolean> useFramework;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      projectDir.setRequired(false);

      projectName.setRequired(true);
      projectName.setLabel("Project Name");

      // context.addFieldRelationship(projectDir).enabledWhen(projectName).isValid();

      projectDir.setDefaultValue(new Callable<File>()
      {
         @Override
         public File call()
         {
            return null;
         }
      });

      // context.addFieldRelationship(useFramework).enabledWhen(projectDir).isValid();
      useFramework.setDefaultValue(false);

      // Define the order in builder

      builder.add(projectName).add(projectDir);
   }

   @Override
   public void validate(UIValidationContext context)
   {
      if (projectName.getValue() == null || projectName.getValue().isEmpty())
      {
         context.addValidationError(projectName, "Project Name should be informed!");
      }
   }

   /*
    *
    * Defines the action to take once inputs are valid and submitted
    */

   @Override
   public NavigationResult next(UIContext context) throws Exception
   {
      if (useFramework.getValue())
      {
         return Results.navigateTo(MockChooseFrameworkStep.class);
      }
      return null;
   }

   /*
    *
    * Defines the action to take once inputs are valid and submitted
    */

   @Override
   public Result execute(UIContext context) throws Exception
   {
      if (projectDir.getValue().mkdirs())
      {
         return Results.success("Success");
      }

      return Results.fail("Reason");

   }

   @Override
   public UICommandMetadata getMetadata()
   {
      // TODO Auto-generated method stub
      return null;
   }
}