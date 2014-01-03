/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.scaffold.impl.ui;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.facets.AbstractFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.scaffold.spi.ScaffoldProvider;
import org.jboss.forge.addon.scaffold.spi.ScaffoldSetupContext;
import org.jboss.forge.addon.scaffold.ui.ScaffoldSetupWizard;
import org.jboss.forge.addon.ui.UICommand;
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
   @WithAttributes(label = "Target Directory")
   private UIInput<String> target;

   @Inject
   @WithAttributes(label = "Overwrite existing files?", defaultValue = "false")
   private UIInput<Boolean> overwrite;

   @Inject
   private Imported<ScaffoldProvider> scaffoldProviders;

   @Inject
   private ProjectFactory factory;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      overwrite.setEnabled(new Callable<Boolean>()
      {
         @Override
         public Boolean call() throws Exception
         {
            ScaffoldProvider selectedProvider = provider.getValue();
            return selectedProvider != null;
         }
      });
      
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
      provider.getValue().validate(validator);
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
      ScaffoldProvider selectedProvider = provider.getValue();
      Project project = getSelectedProject(context);
      ((AbstractFacet) selectedProvider).setFaceted(project);
      UIContext uiContext = context.getUIContext();
      Map<Object, Object> attributeMap = uiContext.getAttributeMap();
      attributeMap.put(ScaffoldProvider.class, selectedProvider);
      attributeMap.put(ScaffoldSetupContext.class, createSetupContext());
      
      // Get the step sequence from the selected scaffold provider
      List<Class<? extends UICommand>> setupFlow = selectedProvider.getSetupFlow();
      
      // Add the execution logic step in the end so that the scaffold setup step is executed last after all other
      // steps
      setupFlow.add(ExecuteSetupStep.class);

      // Extract the first command to obtain the next step
      Class<? extends UICommand> next = setupFlow.remove(0);
      Class<?>[] additional = setupFlow.toArray(new Class<?>[setupFlow.size()]);
      return context.navigateTo(next, (Class<? extends UICommand>[]) additional);
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

   private ScaffoldSetupContext createSetupContext()
   {
      return new ScaffoldSetupContext(target.getValue(), overwrite.getValue());
   }
}
