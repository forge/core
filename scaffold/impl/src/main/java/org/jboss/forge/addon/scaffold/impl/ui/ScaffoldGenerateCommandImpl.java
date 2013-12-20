/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.scaffold.impl.ui;

import java.util.List;

import javax.inject.Inject;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.facets.AbstractFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.scaffold.spi.ScaffoldGenerationContext;
import org.jboss.forge.addon.scaffold.spi.ScaffoldProvider;
import org.jboss.forge.addon.scaffold.ui.ScaffoldGenerateCommand;
import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.services.Imported;

/**
 * Scaffold wizard
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ScaffoldGenerateCommandImpl extends AbstractProjectCommand implements ScaffoldGenerateCommand
{
   @Inject
   @WithAttributes(label = "Scaffold Type", required = true, enabled = false)
   private UISelectOne<ScaffoldProvider> provider;

   @Inject
   @WithAttributes(label = "Target Directory")
   private UIInput<String> target;

   @Inject
   @WithAttributes(label = "Overwrite existing files?")
   private UIInput<Boolean> overwrite;

   @Inject
   private ProjectFactory factory;
   
   @Inject
   private Imported<ScaffoldProvider> scaffoldProviders;
   
   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      provider.setDefaultValue(scaffoldProviders.get());
      provider.setValueChoices(scaffoldProviders);
      provider.setItemLabelConverter(new Converter<ScaffoldProvider, String>()
      {
         
         @Override
         public String convert(ScaffoldProvider source)
         {
            return source == null ? null: source.getName();
         }
      });
      builder.add(provider).add(target).add(overwrite);
   }

   @Override
   public void validate(UIValidationContext validator)
   {
      super.validate(validator);
      ScaffoldProvider scaffold = provider.getValue();
      scaffold.validate(validator);
   }

   @Override
   public Metadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass()).name("Scaffold: Generate")
               .description("Generates the scaffold")
               .category(Categories.create("Scaffold", "Generate"));
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      // No-op. Do nothing.
      return Results.success();
   }
   
   @Override
   public NavigationResult next(UIContext context) throws Exception
   {
      ScaffoldProvider selectedProvider = provider.getValue();
      Project project = getSelectedProject(context);
      context.setAttribute(Project.class, project);
      context.setAttribute(ScaffoldProvider.class, selectedProvider);
      context.setAttribute(ScaffoldGenerationContext.class, populateGenerationContext(context));
      ((AbstractFacet) selectedProvider).setFaceted(project);
      
      // Get the step sequence from the selected scaffold provider
      List<Class<? extends UICommand>> generationFlow = selectedProvider.getGenerationFlow();
      
      // Add the execution logic step in the end so that the scaffold generation step is executed last after all other
      // steps
      generationFlow.add(ExecuteGenerationStep.class);

      // Extract the first command to obtain the next step
      Class<? extends UICommand> next = generationFlow.remove(0);
      Class<?>[] additional = generationFlow.toArray(new Class<?>[generationFlow.size()]);
      return Results.navigateTo(next, (Class<? extends UICommand>[]) additional);
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
   
   private ScaffoldGenerationContext populateGenerationContext(UIContext context)
   {
      ScaffoldGenerationContext generationContext = (ScaffoldGenerationContext) context.getAttribute(ScaffoldGenerationContext.class);
      if(generationContext == null)
      {
         return new ScaffoldGenerationContext(target.getValue(), overwrite.getValue(), null);
      }
      else
      {
         generationContext.setTargetDirectory(target.getValue());
         generationContext.setOverwrite(overwrite.getValue());
         return generationContext;
      }
   }
   
}
