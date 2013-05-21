/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.jpa.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.javaee.jpa.PersistenceContainer;
import org.jboss.forge.addon.javaee.jpa.PersistenceProvider;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.addon.ui.wizard.UIWizard;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.services.ExportedInstance;

public class PersistenceSetupWizard implements UIWizard
{

   @Inject
   @WithAttributes(label = "Container:", required = true, requiredMessage = "Please select a persistence container")
   private UISelectOne<PersistenceContainer> containers;

   @Inject
   @WithAttributes(label = "Provider:", required = true, requiredMessage = "Please select a persistence provider")
   private UISelectOne<PersistenceProvider> providers;

   @Inject
   private AddonRegistry addonRegistry;

   @Override
   public UICommandMetadata getMetadata()
   {
      return Metadata.forCommand(PersistenceSetupWizard.class).name("JPA: Setup")
               .description("Setup JPA in your project").category(Categories.create("JPA"));
   }

   @Override
   public boolean isEnabled(UIContext context)
   {
      return true;
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      initContainers();
      initProviders();
      builder.add(containers).add(providers);
   }

   private void initContainers()
   {
      List<PersistenceContainer> persistenceContainers = new ArrayList<PersistenceContainer>();
      Set<ExportedInstance<PersistenceContainer>> exportedInstances = addonRegistry
               .getExportedInstances(PersistenceContainer.class);
      for (ExportedInstance<PersistenceContainer> exportedInstance : exportedInstances)
      {
         persistenceContainers.add(exportedInstance.get());
      }
      containers.setItemLabelConverter(new Converter<PersistenceContainer, String>()
      {
         @Override
         public String convert(PersistenceContainer source)
         {
            return source != null ? source.getName() : null;
         }
      });
      containers.setValueChoices(persistenceContainers);
   }

   private void initProviders()
   {
      List<PersistenceProvider> persistenceProviders = new ArrayList<PersistenceProvider>();
      Set<ExportedInstance<PersistenceProvider>> exportedInstances = addonRegistry
               .getExportedInstances(PersistenceProvider.class);
      for (ExportedInstance<PersistenceProvider> exportedInstance : exportedInstances)
      {
         persistenceProviders.add(exportedInstance.get());
      }
      providers.setItemLabelConverter(new Converter<PersistenceProvider, String>()
      {
         @Override
         public String convert(PersistenceProvider source)
         {
            return source != null ? source.getName() : null;
         }
      });
      providers.setValueChoices(persistenceProviders);
   }

   @Override
   public void validate(UIValidationContext validator)
   {

   }

   @Override
   public Result execute(UIContext context) throws Exception
   {
      return Results.success();
   }

   @Override
   public NavigationResult next(UIContext context) throws Exception
   {
      return Results.navigateTo(PersistenceSetupDataSourceStep.class);
   }

}
