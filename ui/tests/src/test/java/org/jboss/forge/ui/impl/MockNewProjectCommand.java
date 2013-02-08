package org.jboss.forge.ui.impl;

import java.io.File;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import org.jboss.forge.container.services.Exported;
import org.jboss.forge.ui.Result;
import org.jboss.forge.ui.Results;
import org.jboss.forge.ui.UICommand;
import org.jboss.forge.ui.UICommandMetadata;
import org.jboss.forge.ui.UIContext;
import org.jboss.forge.ui.UIInput;
import org.jboss.forge.ui.UIValidationContext;
import org.jboss.forge.ui.wizard.UIWizard;
import org.jboss.forge.ui.wizard.UIWizardStep;

@Exported
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
   public void initializeUI(UIContext context) throws Exception
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

      context.getUIBuilder().add(projectName).add(projectDir);
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
   public Class<? extends UIWizard> getSuccessor()
   {
      if (useFramework.getValue())
      {
         return MockChooseFrameworkStep.class;
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