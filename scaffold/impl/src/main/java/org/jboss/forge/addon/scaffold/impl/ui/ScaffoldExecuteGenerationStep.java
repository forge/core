/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.scaffold.impl.ui;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.scaffold.spi.ResourceCollection;
import org.jboss.forge.addon.scaffold.spi.ScaffoldGenerationContext;
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
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;

public class ScaffoldExecuteGenerationStep extends AbstractProjectCommand implements UIWizardStep
{
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
      Map<Object, Object> attributeMap = context.getUIContext().getAttributeMap();
      ScaffoldProvider selectedProvider = (ScaffoldProvider) attributeMap.get(ScaffoldProvider.class);
      Object requiresScaffoldSetup = attributeMap.get(ScaffoldGenerateCommandImpl.REQUIRES_SCAFFOLD_SETUP);
      if (requiresScaffoldSetup != null && (boolean) requiresScaffoldSetup == true)
      {
         ScaffoldSetupContext setupContext = (ScaffoldSetupContext) attributeMap.get(ScaffoldSetupContext.class);
         selectedProvider.setup(setupContext);
         getProjectFactory().invalidateCaches();
      }
      ResourceCollection resourceCollection = (ResourceCollection) attributeMap.get(ResourceCollection.class);
      // Ensure that the resource collection is instantiated. Prevents a null check in the scaffold provider.
      Collection<Resource<?>> resources = resourceCollection != null ? resourceCollection.getResources()
               : Collections.<Resource<?>> emptySet();
      selectedProvider.generateFrom(populateGenerationContext(context.getUIContext(), resources));
      return Results.success("Scaffold was generated successfully.");
   }

   @Override
   public void validate(UIValidationContext context)
   {
      // No op
   }

   @Override
   protected boolean isProjectRequired()
   {
      return true;
   }

   @Override
   protected ProjectFactory getProjectFactory()
   {
      return SimpleContainer.getServices(getClass().getClassLoader(), ProjectFactory.class).get();
   }

   private ScaffoldGenerationContext populateGenerationContext(UIContext context, Collection<Resource<?>> resources)
   {
      Map<Object, Object> attributeMap = context.getAttributeMap();
      ScaffoldGenerationContext generationContext = (ScaffoldGenerationContext) attributeMap
               .get(ScaffoldGenerationContext.class);
      generationContext.setResources(resources);
      generationContext.setProject(getSelectedProject(context));
      return generationContext;
   }

}
