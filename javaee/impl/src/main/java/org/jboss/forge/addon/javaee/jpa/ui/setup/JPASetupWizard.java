/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.jpa.ui.setup;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeSet;

import javax.inject.Inject;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.javaee.jpa.JPAFacet;
import org.jboss.forge.addon.javaee.jpa.PersistenceContainer;
import org.jboss.forge.addon.javaee.jpa.PersistenceMetaModelFacet;
import org.jboss.forge.addon.javaee.jpa.PersistenceProvider;
import org.jboss.forge.addon.javaee.jpa.containers.JBossEAP6Container;
import org.jboss.forge.addon.javaee.jpa.providers.JavaEEDefaultProvider;
import org.jboss.forge.addon.javaee.ui.AbstractJavaEECommand;
import org.jboss.forge.addon.projects.Project;
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
import org.jboss.forge.addon.ui.wizard.UIWizard;
import org.jboss.forge.furnace.services.Imported;

public class JPASetupWizard extends AbstractJavaEECommand implements UIWizard
{
   @Inject
   @WithAttributes(shortName = 'j', label = "JPA Version", required = true, defaultValue = "2.0")
   private UISelectOne<JPAFacet<?>> jpaVersion;

   @Inject
   @WithAttributes(shortName = 'c', label = "Container", required = true)
   private UISelectOne<PersistenceContainer> container;

   @Inject
   @WithAttributes(shortName = 'p', label = "Provider", required = true)
   private UISelectOne<PersistenceProvider> provider;

   @Inject
   @WithAttributes(shortName = 'm', label = "Install a JPA 2 metamodel generator?")
   private UIInput<Boolean> configureMetadata;

   @Inject
   private JBossEAP6Container defaultContainer;

   @Inject
   private JavaEEDefaultProvider defaultProvider;

   @Inject
   private Imported<PersistenceMetaModelFacet> metaModelFacets;

   @Inject
   private FacetFactory facetFactory;

   @Override
   public Metadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass()).name("JPA: Setup")
               .description("Setup JPA in your project")
               .category(Categories.create(super.getMetadata(context).getCategory().getName(), "JPA"));
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      initContainers(builder.getUIContext());
      initProviders();
      initConfigureMetadata();
      builder.add(jpaVersion).add(container).add(provider).add(configureMetadata);
   }

   private void initContainers(UIContext context)
   {
      final boolean isGUI = context.getProvider().isGUI();
      container.setItemLabelConverter(new Converter<PersistenceContainer, String>()
      {
         @Override
         public String convert(PersistenceContainer source)
         {
            return source != null ? source.getName(isGUI) : null;
         }
      });
      // Ordering items:
      TreeSet<PersistenceContainer> treeSet = new TreeSet<>(new Comparator<PersistenceContainer>()
      {
         @Override
         public int compare(PersistenceContainer o1, PersistenceContainer o2)
         {
            return String.valueOf(o1.getName(isGUI)).compareTo(o2.getName(isGUI));
         }
      });
      Iterable<PersistenceContainer> valueChoices = container.getValueChoices();
      for (PersistenceContainer persistenceContainer : valueChoices)
      {
         treeSet.add(persistenceContainer);
      }
      container.setValueChoices(treeSet);
      container.setDefaultValue(defaultContainer);
   }

   private void initProviders()
   {
      provider.setItemLabelConverter(new Converter<PersistenceProvider, String>()
      {
         @Override
         public String convert(PersistenceProvider source)
         {
            return source != null ? source.getName() : null;
         }
      });
      provider.setDefaultValue(defaultProvider);
   }

   private void initConfigureMetadata()
   {
      configureMetadata.setDefaultValue(Boolean.FALSE);
      if (metaModelFacets.isUnsatisfied())
      {
         configureMetadata.setEnabled(false);
      }
   }

   @Override
   public void validate(UIValidationContext validator)
   {
      // NOOP
   }

   @Override
   public Result execute(final UIExecutionContext context) throws Exception
   {
      applyUIValues(context.getUIContext());
      Project project = getSelectedProject(context);
      JPAFacet<?> facet = jpaVersion.getValue();
      if (facetFactory.install(project, facet))
      {
         return Results.success();
      }
      return Results.fail("Could not install JPA.");
   }

   @Override
   public NavigationResult next(UINavigationContext context) throws Exception
   {
      applyUIValues(context.getUIContext());
      return Results.navigateTo(JPASetupConnectionStep.class);
   }

   private void applyUIValues(final UIContext context)
   {
      Map<Object, Object> attributeMap = context.getAttributeMap();
      attributeMap.put(JPAFacet.class, jpaVersion.getValue());
      attributeMap.put(PersistenceProvider.class, provider.getValue());
      attributeMap.put(PersistenceContainer.class, container.getValue());
      attributeMap.put("ConfigureMetadata", configureMetadata.getValue());
   }

   @Override
   protected boolean isProjectRequired()
   {
      return true;
   }
}
