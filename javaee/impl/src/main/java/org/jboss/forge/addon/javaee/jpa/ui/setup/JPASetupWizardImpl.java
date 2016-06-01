/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.jpa.ui.setup;

import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;

import javax.inject.Inject;

import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.javaee.jpa.JPAFacet;
import org.jboss.forge.addon.javaee.jpa.PersistenceContainer;
import org.jboss.forge.addon.javaee.jpa.PersistenceMetaModelFacet;
import org.jboss.forge.addon.javaee.jpa.PersistenceProvider;
import org.jboss.forge.addon.javaee.jpa.containers.JBossEAP6Container;
import org.jboss.forge.addon.javaee.jpa.containers.WildflyContainer;
import org.jboss.forge.addon.javaee.jpa.providers.HibernateProvider;
import org.jboss.forge.addon.javaee.ui.AbstractJavaEECommand;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.stacks.Stack;
import org.jboss.forge.addon.projects.stacks.annotations.StackConstraint;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UINavigationContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.services.Imported;

@FacetConstraint(JavaSourceFacet.class)
@StackConstraint(JPAFacet.class)
public class JPASetupWizardImpl extends AbstractJavaEECommand implements JPASetupWizard
{
   @Inject
   @WithAttributes(shortName = 'j', label = "JPA Version", required = true, defaultValue = "2.0")
   private UISelectOne<JPAFacet<?>> jpaVersion;

   @Inject
   @WithAttributes(shortName = 'c', label = "Container", required = true)
   private UISelectOne<PersistenceContainer> jpaContainer;

   @Inject
   @WithAttributes(shortName = 'p', label = "Provider", required = true)
   private UISelectOne<PersistenceProvider> jpaProvider;

   @Inject
   @WithAttributes(shortName = 'm', label = "Install a JPA 2 metamodel generator?")
   private UIInput<Boolean> configureMetadata;

   @Inject
   private JBossEAP6Container defaultContainer;

   @Inject
   private WildflyContainer wildFlyContainer;

   @Inject
   private HibernateProvider defaultProvider;

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
      UIContext uiContext = builder.getUIContext();
      Project project = getSelectedProject(builder);
      if (initJpaVersion(project, uiContext))
      {
         builder.add(jpaVersion);
      }
      initContainers(project, uiContext);
      initProviders();
      initConfigureMetadata();
      builder.add(jpaContainer).add(jpaProvider).add(configureMetadata);
   }

   private boolean initJpaVersion(Project project, UIContext context)
   {
      if (project.hasFacet(JPAFacet.class))
      {
         // If JPA is already installed, do not ask for JPA Version
         jpaVersion.setEnabled(false);
         return false;
      }
      else
      {
         return filterValueChoicesFromStack(project, jpaVersion);
      }
   }

   private void initContainers(Project project, UIContext context)
   {
      final boolean isGUI = context.getProvider().isGUI();
      jpaContainer.setItemLabelConverter((source) -> source.getName(isGUI));
      // Ordering items
      TreeSet<PersistenceContainer> treeSet = new TreeSet<>(
               (o1, o2) -> String.valueOf(o1.getName(isGUI)).compareTo(o2.getName(isGUI)));
      Optional<Stack> stack = project.getStack();
      for (PersistenceContainer persistenceContainer : jpaContainer.getValueChoices())
      {
         if (!stack.isPresent() || persistenceContainer.supports(stack.get()))
            treeSet.add(persistenceContainer);
      }
      jpaContainer.setValueChoices(treeSet);
      if (treeSet.contains(defaultContainer))
      {
         jpaContainer.setDefaultValue(defaultContainer);
      }
      else if (treeSet.contains(wildFlyContainer))
      {
         jpaContainer.setDefaultValue(wildFlyContainer);
      }
   }

   private void initProviders()
   {
      jpaProvider.setItemLabelConverter((source) -> source.getName());
      jpaProvider.setDefaultValue(defaultProvider);
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
   public Result execute(final UIExecutionContext context) throws Exception
   {
      applyUIValues(context.getUIContext());
      Project project = getSelectedProject(context);
      if (jpaVersion.isEnabled())
      {
         JPAFacet<?> facet = jpaVersion.getValue();
         if (facetFactory.install(project, facet))
         {
            context.getUIContext().setSelection(facet.getConfigFile());
            return Results.success();
         }
      }
      else
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
      attributeMap.put(PersistenceProvider.class, jpaProvider.getValue());
      attributeMap.put(PersistenceContainer.class, jpaContainer.getValue());
      attributeMap.put("ConfigureMetadata", configureMetadata.getValue());
   }

   @Override
   protected boolean isProjectRequired()
   {
      return true;
   }
}
