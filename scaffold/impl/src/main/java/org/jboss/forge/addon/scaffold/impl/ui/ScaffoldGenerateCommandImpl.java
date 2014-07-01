/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.scaffold.impl.ui;

import java.util.Iterator;
import java.util.Map;

import javax.inject.Inject;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.scaffold.spi.ScaffoldGenerationContext;
import org.jboss.forge.addon.scaffold.spi.ScaffoldProvider;
import org.jboss.forge.addon.scaffold.spi.ScaffoldSetupContext;
import org.jboss.forge.addon.scaffold.ui.ScaffoldGenerateCommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UINavigationContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.result.navigation.NavigationResultBuilder;
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
   public static final String REQUIRES_SCAFFOLD_SETUP = "REQUIRES_SCAFFOLD_SETUP";

   @Inject
   @WithAttributes(label = "Scaffold Type", required = true)
   private UISelectOne<ScaffoldProvider> provider;

   @Inject
   @WithAttributes(label = "Web Root Path", defaultValue = "/", description = "The web root path where the scaffolding will be "
            + "placed/accessible from the client browser (default '/').")
   private UIInput<String> webRoot;

   @Inject
   private ProjectFactory factory;

   @Inject
   private Imported<ScaffoldProvider> scaffoldProviders;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      Iterator<ScaffoldProvider> scaffolds = scaffoldProviders.iterator();
      if (scaffolds.hasNext())
      {
         provider.setDefaultValue(scaffolds.next());
      }
      provider.setValueChoices(scaffoldProviders);
      provider.setItemLabelConverter(new Converter<ScaffoldProvider, String>()
      {

         @Override
         public String convert(ScaffoldProvider source)
         {
            return source == null ? null : source.getName();
         }
      });
      builder.add(provider).add(webRoot);
   }

   @Override
   public void validate(UIValidationContext validator)
   {
      super.validate(validator);
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
   public NavigationResult next(UINavigationContext context) throws Exception
   {
      ScaffoldProvider selectedProvider = provider.getValue();
      UIContext uiContext = context.getUIContext();
      Map<Object, Object> attributeMap = uiContext.getAttributeMap();

      attributeMap.put(ScaffoldProvider.class, selectedProvider);
      ScaffoldGenerationContext generationContext = populateGenerationContext(uiContext);
      attributeMap.put(ScaffoldGenerationContext.class, generationContext);

      NavigationResult setupFlow = null;
      Project project = getSelectedProject(uiContext);

      // Verify if the selected provider is installed
      // If not, add the setup flow and inform the generation step to setup the scaffold.
      ScaffoldSetupContext setupContext = populateSetupContext(uiContext);
      if (!selectedProvider.isSetup(setupContext))
      {
         setupFlow = selectedProvider.getSetupFlow(setupContext);
         attributeMap.put(REQUIRES_SCAFFOLD_SETUP, true);
         attributeMap.put(ScaffoldSetupContext.class, setupContext);
      }

      // Get the step sequence from the selected scaffold provider
      NavigationResult generationFlow = selectedProvider.getGenerationFlow(generationContext);

      // Add the execution logic step in the end so that the scaffold generation step is executed last after all other
      // steps
      NavigationResultBuilder builder = NavigationResultBuilder.create(setupFlow);
      NavigationResult navigationResult = builder.add(generationFlow).add(ScaffoldExecuteGenerationStep.class).build();

      return navigationResult;
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
      Project project = getSelectedProject(context);
      Map<Object, Object> attributeMap = context.getAttributeMap();
      String targetDir = webRoot.getValue();
      if(targetDir == null || targetDir.equals("/"))
      {
         targetDir = "";
      }
      ScaffoldGenerationContext generationContext = (ScaffoldGenerationContext) attributeMap
               .get(ScaffoldGenerationContext.class);
      if (generationContext == null)
      {
         return new ScaffoldGenerationContext(targetDir, null, project);
      }
      else
      {
         generationContext.setTargetDirectory(targetDir);
         return generationContext;
      }
   }

   private ScaffoldSetupContext populateSetupContext(UIContext context)
   {
      Project project = getSelectedProject(context);
      String targetDir = webRoot.getValue();
      if(targetDir == null || targetDir.equals("/"))
      {
         targetDir = "";
      }
      return new ScaffoldSetupContext(targetDir, project);
   }

}
