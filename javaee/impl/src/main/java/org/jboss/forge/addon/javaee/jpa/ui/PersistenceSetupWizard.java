/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.jpa.ui;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import javax.inject.Inject;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.javaee.jpa.PersistenceContainer;
import org.jboss.forge.addon.javaee.jpa.PersistenceProvider;
import org.jboss.forge.addon.javaee.jpa.containers.JBossEAP6Container;
import org.jboss.forge.addon.javaee.jpa.providers.HibernateProvider;
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
   private JBossEAP6Container defaultContainer;

   @Inject
   private HibernateProvider defaultProvider;

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
      Set<PersistenceContainer> persistenceContainers = new TreeSet<PersistenceContainer>(
               new Comparator<PersistenceContainer>()
               {
                  @Override
                  public int compare(PersistenceContainer o1, PersistenceContainer o2)
                  {
                     return o1.getName().compareTo(o2.getName());
                  }
               });
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
      containers.setDefaultValue(defaultContainer);
   }

   private void initProviders()
   {
      Set<PersistenceProvider> persistenceProviders = new TreeSet<PersistenceProvider>(
               new Comparator<PersistenceProvider>()
               {
                  @Override
                  public int compare(PersistenceProvider o1, PersistenceProvider o2)
                  {
                     return o1.getName().compareTo(o2.getName());
                  }
               });
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
      providers.setDefaultValue(defaultProvider);
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
