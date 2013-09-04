/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.jpa.ui.setup;

import javax.inject.Inject;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.javaee.jpa.PersistenceContainer;
import org.jboss.forge.addon.javaee.jpa.PersistenceProvider;
import org.jboss.forge.addon.javaee.jpa.containers.JBossEAP6Container;
import org.jboss.forge.addon.javaee.jpa.providers.HibernateProvider;
import org.jboss.forge.addon.javaee.ui.AbstractJavaEECommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.addon.ui.wizard.UIWizard;

public class PersistenceSetupWizard extends AbstractJavaEECommand implements UIWizard
{

   @Inject
   @WithAttributes(label = "Container", required = true)
   private UISelectOne<PersistenceContainer> containers;

   @Inject
   @WithAttributes(label = "Provider", required = true)
   private UISelectOne<PersistenceProvider> providers;

   @Inject
   @WithAttributes(label = "Install a JPA 2 metamodel generator?")
   private UIInput<Boolean> configureMetadata;

   @Inject
   private JBossEAP6Container defaultContainer;

   @Inject
   private HibernateProvider defaultProvider;

   @Override
   public Metadata getMetadata()
   {
      Metadata metadata = super.getMetadata();
      return metadata.name("JPA: Setup")
               .description("Setup JPA in your project")
               .category(Categories.create(metadata.getCategory().getName(), "JPA"));
   }

   /**
    * Return true only if a project is selected
    */
   @Override
   public boolean isEnabled(UIContext context)
   {
      return containsProject(context);
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      initContainers();
      initProviders();
      initConfigureMetadata();
      builder.add(containers).add(providers).add(configureMetadata);
   }

   private void initContainers()
   {
      containers.setItemLabelConverter(new Converter<PersistenceContainer, String>()
      {
         @Override
         public String convert(PersistenceContainer source)
         {
            return source != null ? source.getName() : null;
         }
      });
      containers.setDefaultValue(defaultContainer);
   }

   private void initProviders()
   {
      providers.setItemLabelConverter(new Converter<PersistenceProvider, String>()
      {
         @Override
         public String convert(PersistenceProvider source)
         {
            return source != null ? source.getName() : null;
         }
      });
      providers.setDefaultValue(defaultProvider);
   }

   private void initConfigureMetadata()
   {
      configureMetadata.setDefaultValue(Boolean.FALSE);
   }

   @Override
   public void validate(UIValidationContext validator)
   {
      // NOOP
   }

   @Override
   public Result execute(final UIContext context) throws Exception
   {
      applyUIValues(context);
      return Results.success();
   }

   @Override
   public NavigationResult next(UIContext context) throws Exception
   {
      applyUIValues(context);
      return Results.navigateTo(PersistenceSetupConnectionStep.class);
   }

   private void applyUIValues(final UIContext context)
   {
      context.setAttribute(PersistenceProvider.class, providers.getValue());
      context.setAttribute(PersistenceContainer.class, containers.getValue());
      context.setAttribute("ConfigureMetadata", configureMetadata.getValue());
   }

   public UISelectOne<PersistenceContainer> getContainers()
   {
      return containers;
   }

   public UISelectOne<PersistenceProvider> getProviders()
   {
      return providers;
   }

   public UIInput<Boolean> getConfigureMetadata()
   {
      return configureMetadata;
   }
}
