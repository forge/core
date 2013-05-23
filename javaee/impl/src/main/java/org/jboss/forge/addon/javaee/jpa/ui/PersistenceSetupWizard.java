/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.jpa.ui;

import javax.inject.Inject;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.javaee.jpa.PersistenceContainer;
import org.jboss.forge.addon.javaee.jpa.PersistenceProvider;
import org.jboss.forge.addon.javaee.jpa.containers.JBossEAP6Container;
import org.jboss.forge.addon.javaee.jpa.providers.HibernateProvider;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.addon.ui.wizard.UIWizard;

public class PersistenceSetupWizard implements UIWizard
{

   @Inject
   @WithAttributes(label = "Container:", required = true, requiredMessage = "Please select a persistence container")
   private UISelectOne<PersistenceContainer> containers;

   @Inject
   @WithAttributes(label = "Provider:", required = true, requiredMessage = "Please select a persistence provider")
   private UISelectOne<PersistenceProvider> providers;

   @Inject
   @WithAttributes(label = "Install a JPA 2 metamodel generator?")
   private UIInput<Boolean> configureMetadata;

   @Inject
   private JBossEAP6Container defaultContainer;

   @Inject
   private HibernateProvider defaultProvider;

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

   }

   @Override
   public Result execute(UIContext context) throws Exception
   {
      context.setAttribute(PersistenceProvider.class, providers.getValue());
      context.setAttribute(PersistenceContainer.class, containers.getValue());
      return Results.success();
   }

   @Override
   public NavigationResult next(UIContext context) throws Exception
   {
      context.setAttribute(PersistenceProvider.class, providers.getValue());
      PersistenceContainer container = containers.getValue();
      context.setAttribute(PersistenceContainer.class, container);
      return Results.navigateTo(PersistenceSetupConnectionStep.class);
   }

}
