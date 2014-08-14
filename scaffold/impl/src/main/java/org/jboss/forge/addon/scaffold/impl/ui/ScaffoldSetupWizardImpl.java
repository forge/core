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
import org.jboss.forge.addon.scaffold.spi.ScaffoldProvider;
import org.jboss.forge.addon.scaffold.spi.ScaffoldSetupContext;
import org.jboss.forge.addon.scaffold.ui.ScaffoldSetupWizard;
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
public class ScaffoldSetupWizardImpl extends AbstractProjectCommand implements ScaffoldSetupWizard
{
   @Inject
   @WithAttributes(label = "Provider", required = true)
   private UISelectOne<ScaffoldProvider> provider;

   @Inject
   @WithAttributes(label = "Web Root Path", defaultValue = "/", description = "The web root path where the scaffolding will be "
            + "placed/accessible from the client browser (default '/').")
   private UIInput<String> webRoot;

   @Inject
   private Imported<ScaffoldProvider> scaffoldProviders;

   @Inject
   private ProjectFactory factory;

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
      return Metadata.from(super.getMetadata(context), getClass()).name("Scaffold: Setup")
               .description("Setup the scaffold")
               .category(Categories.create("Scaffold", "Setup"));
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      // No-op. Scaffold setup is done in a separate step.
      return Results.success();
   }

   @Override
   public NavigationResult next(UINavigationContext context) throws Exception
   {
      UIContext uiContext = context.getUIContext();
      Map<Object, Object> attributeMap = uiContext.getAttributeMap();

      ScaffoldProvider selectedProvider = provider.getValue();
      ScaffoldSetupContext setupContext = createSetupContext(uiContext);
      attributeMap.put(ScaffoldProvider.class, selectedProvider);
      attributeMap.put(ScaffoldSetupContext.class, setupContext);

      // Get the navigation result entries from the selected scaffold provider
      NavigationResult setupFlow = selectedProvider.getSetupFlow(setupContext);

      // Add the execution logic step in the end so that the scaffold setup step is executed last after all other steps
      NavigationResultBuilder builder = NavigationResultBuilder.create(setupFlow);
      NavigationResult navigationResult = builder.add(ScaffoldExecuteSetupStep.class).build();

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

   private ScaffoldSetupContext createSetupContext(UIContext context)
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
