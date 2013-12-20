package org.jboss.forge.addon.scaffold.impl.ui;

import javax.inject.Inject;

import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.scaffold.spi.ScaffoldProvider;
import org.jboss.forge.addon.scaffold.spi.ScaffoldSetupContext;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UINavigationContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;

public class ExecuteSetupStep extends AbstractProjectCommand implements UIWizardStep
{

   @Inject
   private ProjectFactory factory;
   
   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(getClass()).name("Scaffold: Setup")
               .description("Setup the scaffold");
   }

   @Override
   public boolean isEnabled(UIContext context)
   {
      return true;
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      // No-op. This command has no UI.
   }
   
   @Override
   public NavigationResult next(UINavigationContext context) throws Exception
   {
      // This is the last step
      return null;
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      ScaffoldProvider selectedProvider = (ScaffoldProvider) context.getUIContext()
               .getAttribute(ScaffoldProvider.class);
      ScaffoldSetupContext setupContext = (ScaffoldSetupContext) context.getUIContext().getAttribute(
               ScaffoldSetupContext.class);
      selectedProvider.setup(getSelectedProject(context), setupContext);
      // No-op. Scaffold setup is done in a separate step.
      return Results.success("Scaffold was setup successfully.");
   }

   @Override
   public void validate(UIValidationContext context)
   {
      //No-op. Nothing to validate here.
   }

   @Override
   protected boolean isProjectRequired()
   {
      return true;
   }

   @Override
   protected ProjectFactory getProjectFactory()
   {
      return factory;
   }

}
