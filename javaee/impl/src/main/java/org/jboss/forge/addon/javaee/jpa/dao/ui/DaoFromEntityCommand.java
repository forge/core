/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.jpa.dao.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.Id;

import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.javaee.ejb.EJBFacet;
import org.jboss.forge.addon.javaee.ejb.ui.EJBSetupWizardImpl;
import org.jboss.forge.addon.javaee.jpa.JPAFacet;
import org.jboss.forge.addon.javaee.jpa.dao.DaoGenerationContext;
import org.jboss.forge.addon.javaee.jpa.dao.DaoResourceGenerator;
import org.jboss.forge.addon.javaee.jpa.ui.setup.JPASetupWizard;
import org.jboss.forge.addon.javaee.ui.AbstractJavaEECommand;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.ui.command.PrerequisiteCommandsProvider;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectMany;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.result.navigation.NavigationResultBuilder;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.roaster.model.Member;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.shrinkwrap.descriptor.api.persistence.PersistenceCommonDescriptor;
import org.jboss.shrinkwrap.descriptor.api.persistence.PersistenceUnitCommon;

/**
 * Command used to generate DAOs from Entities
 * 
 * @author <a href="salem.elrahal@gmail.com">Salem Elrahal</a>
 *
 */
@FacetConstraint(JavaSourceFacet.class)
public class DaoFromEntityCommand extends AbstractJavaEECommand implements PrerequisiteCommandsProvider
{

   @Inject
   @WithAttributes(label = "Targets", required = true)
   private UISelectMany<JavaClassSource> targets;

   @Inject
   @WithAttributes(label = "Generator", required = true)
   private UISelectOne<DaoResourceGenerator> generator;

   @Inject
   @WithAttributes(label = "Persistence Unit", required = true)
   private UISelectOne<String> persistenceUnit;

   @Inject
   @WithAttributes(label = "Target Package Name", required = true, type = InputType.JAVA_PACKAGE_PICKER)
   private UIInput<String> packageName;

   @Inject
   @WithAttributes(label = "Overwrite existing classes?", enabled = false, defaultValue = "false")
   private UIInput<Boolean> overwrite;

   @Inject
   private DaoResourceGenerator defaultResourceGenerator;

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass()).name("JPA: Generate DAOs From Entities")
               .description("Generate DAOs from JPA entities")
               .category(Categories.create(super.getMetadata(context).getCategory(), "JPA"));
   }

   @SuppressWarnings({ "rawtypes", "unchecked" })
   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      UIContext context = builder.getUIContext();
      Project project = getSelectedProject(context);
      JPAFacet<PersistenceCommonDescriptor> persistenceFacet = project.getFacet(JPAFacet.class);
      JavaSourceFacet javaSourceFacet = project.getFacet(JavaSourceFacet.class);
      List<JavaClassSource> allEntities = persistenceFacet.getAllEntities();
      List<JavaClassSource> supportedEntities = new ArrayList<>();
      for (JavaClassSource entity : allEntities)
      {
         if (isEntityWithSimpleKey(entity))
         {
            supportedEntities.add(entity);
         }
      }
      targets.setValueChoices(supportedEntities);
      targets.setItemLabelConverter(JavaClassSource::getQualifiedName);
      List<String> persistenceUnits = new ArrayList<>();
      List<PersistenceUnitCommon> allUnits = persistenceFacet.getConfig().getAllPersistenceUnit();
      for (PersistenceUnitCommon persistenceUnit : allUnits)
      {
         persistenceUnits.add(persistenceUnit.getName());
      }
      if (!persistenceUnits.isEmpty())
      {
         persistenceUnit.setValueChoices(persistenceUnits).setDefaultValue(persistenceUnits.get(0));
      }

      // TODO: May detect where @Path resources are located
      packageName.setDefaultValue(javaSourceFacet.getBasePackage() + ".dao");

      generator.setDefaultValue(defaultResourceGenerator);
      if (context.getProvider().isGUI())
      {
         generator.setItemLabelConverter(DaoResourceGenerator::getDescription);
      }
      else
      {
         generator.setItemLabelConverter(DaoResourceGenerator::getName);
      }
      builder.add(targets)
               .add(generator)
               .add(packageName)
               .add(persistenceUnit)
               .add(overwrite);
   }

   private boolean isEntityWithSimpleKey(JavaClassSource entity)
   {
      for (Member<?> member : entity.getMembers())
      {
         // FORGE-823 Only add entities with @Id as valid entities for Dao resource generation.
         // Composite keys are not yet supported.
         if (member.hasAnnotation(Id.class))
         {
            return true;
         }
      }
      return false;
   }

   @Override
   public Result execute(final UIExecutionContext context) throws Exception
   {
      UIContext uiContext = context.getUIContext();
      DaoGenerationContext generationContext = createContextFor(uiContext);
      Set<JavaClassSource> endpoints = generateDaos(generationContext);
      Project project = generationContext.getProject();
      JavaSourceFacet javaSourceFacet = project.getFacet(JavaSourceFacet.class);
      List<JavaResource> selection = new ArrayList<>();

      for (JavaClassSource javaClass : endpoints)
      {
         selection.add(javaSourceFacet.saveJavaSource(javaClass));
      }
      uiContext.setSelection(selection);
      return Results.success("Dao created");
   }

   private Set<JavaClassSource> generateDaos(DaoGenerationContext generationContext) throws Exception
   {
      DaoResourceGenerator selectedGenerator = generator.getValue();
      Set<JavaClassSource> classes = new HashSet<>();
      for (JavaClassSource target : targets.getValue())
      {
         generationContext.setEntity(target);
         List<JavaClassSource> artifacts = selectedGenerator.generateFrom(generationContext);
         classes.addAll(artifacts);
      }
      return classes;
   }

   @Override
   protected boolean isProjectRequired()
   {
      return true;
   }

   private DaoGenerationContext createContextFor(final UIContext context)
   {
      DaoGenerationContext generationContext = new DaoGenerationContext();
      generationContext.setProject(getSelectedProject(context));
      generationContext.setPersistenceUnitName(persistenceUnit.getValue());
      generationContext.setTargetPackageName(packageName.getValue());
      return generationContext;
   }

   @Override
   public NavigationResult getPrerequisiteCommands(UIContext context)
   {
      NavigationResultBuilder builder = NavigationResultBuilder.create();
      Project project = getSelectedProject(context);
      if (project != null)
      {
         if (!project.hasFacet(JPAFacet.class))
         {
            builder.add(JPASetupWizard.class);
         }
         if (!project.hasFacet(EJBFacet.class))
         {
            builder.add(EJBSetupWizardImpl.class);
         }
      }
      return builder.build();
   }

}
