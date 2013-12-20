package org.jboss.forge.addon.scaffold.impl.ui;

import java.util.Collection;

import javax.inject.Inject;

import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.scaffold.spi.ResourceCollection;
import org.jboss.forge.addon.scaffold.spi.ScaffoldGenerationContext;
import org.jboss.forge.addon.scaffold.spi.ScaffoldProvider;
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

public class ExecuteGenerationStep extends AbstractProjectCommand implements UIWizardStep
{
   
   @Inject
   private ProjectFactory factory;

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass()).name("Scaffold: Generate")
               .description("Generate the scaffold");
   }

   @Override
   public boolean isEnabled(UIContext context)
   {
      return true;
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      // No-op! No UI is necessary for this wizard step.
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
      ScaffoldProvider selectedProvider = (ScaffoldProvider) context.getUIContext().getAttribute(ScaffoldProvider.class);
      ResourceCollection resourceCollection = (ResourceCollection) context.getUIContext().getAttribute(ResourceCollection.class);
      selectedProvider.generateFrom(getSelectedProject(context), populateGenerationContext(context.getUIContext(), resourceCollection.getResources()));
      return Results.success("Scaffold was generated successfully.");
   }

   @Override
   public void validate(UIValidationContext context)
   {
      //No op
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
   
   private ScaffoldGenerationContext populateGenerationContext(UIContext context, Collection resources)
   {
      ScaffoldGenerationContext generationContext = (ScaffoldGenerationContext) context.getAttribute(ScaffoldGenerationContext.class);
      generationContext.setResources(resources);
      return generationContext;
   }

}
