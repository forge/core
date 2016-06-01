/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.rest.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.Id;
import javax.ws.rs.core.MediaType;

import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.javaee.ejb.EJBFacet;
import org.jboss.forge.addon.javaee.ejb.ui.EJBSetupWizardImpl;
import org.jboss.forge.addon.javaee.jpa.JPAFacet;
import org.jboss.forge.addon.javaee.jpa.ui.setup.JPASetupWizard;
import org.jboss.forge.addon.javaee.rest.RestFacet;
import org.jboss.forge.addon.javaee.rest.generation.RestGenerationContext;
import org.jboss.forge.addon.javaee.rest.generation.RestResourceGenerator;
import org.jboss.forge.addon.javaee.rest.generator.impl.EntityBasedResourceGenerator;
import org.jboss.forge.addon.javaee.ui.AbstractJavaEECommand;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.stacks.annotations.StackConstraint;
import org.jboss.forge.addon.text.Inflector;
import org.jboss.forge.addon.ui.command.PrerequisiteCommandsProvider;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UIInputMany;
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
import org.jboss.forge.furnace.util.Lists;
import org.jboss.forge.roaster.model.Member;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.shrinkwrap.descriptor.api.persistence.PersistenceCommonDescriptor;
import org.jboss.shrinkwrap.descriptor.api.persistence.PersistenceUnitCommon;

/**
 * Generates REST endpoints from JPA Entities
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@FacetConstraint(JavaSourceFacet.class)
@StackConstraint(RestFacet.class)
public class RestEndpointFromEntityCommand extends AbstractJavaEECommand implements PrerequisiteCommandsProvider
{
   @Inject
   @WithAttributes(label = "Content Type", defaultValue = MediaType.APPLICATION_JSON, required = true)
   private UIInputMany<String> contentType;

   @Inject
   @WithAttributes(label = "Targets", required = true)
   private UISelectMany<JavaClassSource> targets;

   @Inject
   @WithAttributes(label = "Generator", required = true)
   private UISelectOne<RestResourceGenerator> generator;

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
   private EntityBasedResourceGenerator defaultResourceGenerator;

   @Inject
   private Inflector inflector;

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass()).name("REST: Generate Endpoints From Entities")
               .description("Generate REST endpoints from JPA entities")
               .category(Categories.create(super.getMetadata(context).getCategory(), "JAX-RS"));
   }

   @SuppressWarnings({ "rawtypes", "unchecked" })
   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      UIContext context = builder.getUIContext();
      Project project = getSelectedProject(context);
      List<JavaClassSource> supportedEntities = new ArrayList<>();
      List<String> persistenceUnits = new ArrayList<>();
      JavaSourceFacet javaSourceFacet = project.getFacet(JavaSourceFacet.class);
      if (project.hasFacet(JPAFacet.class))
      {
         JPAFacet<PersistenceCommonDescriptor> persistenceFacet = project.getFacet(JPAFacet.class);
         List<JavaClassSource> allEntities = persistenceFacet.getAllEntities();
         for (JavaClassSource entity : allEntities)
         {
            if (isEntityWithSimpleKey(entity))
            {
               supportedEntities.add(entity);
            }
         }
         List<PersistenceUnitCommon> allUnits = persistenceFacet.getConfig().getAllPersistenceUnit();
         for (PersistenceUnitCommon persistenceUnit : allUnits)
         {
            persistenceUnits.add(persistenceUnit.getName());
         }
      }
      targets.setValueChoices(supportedEntities);
      targets.setItemLabelConverter((source) -> source.getQualifiedName());
      if (!persistenceUnits.isEmpty())
      {
         persistenceUnit.setValueChoices(persistenceUnits).setDefaultValue(persistenceUnits.get(0));
      }

      // TODO: May detect where @Path resources are located
      packageName.setDefaultValue(javaSourceFacet.getBasePackage() + ".rest");

      contentType.setCompleter(
               (uiContext, input, value) -> Arrays.asList(MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON));
      generator.setDefaultValue(defaultResourceGenerator);
      if (context.getProvider().isGUI())
      {
         generator.setItemLabelConverter((source) -> source.getDescription());
      }
      else
      {
         generator.setItemLabelConverter((source) -> source.getName());
      }
      builder.add(targets)
               .add(generator)
               .add(contentType)
               .add(packageName)
               .add(persistenceUnit)
               .add(overwrite);
   }

   private boolean isEntityWithSimpleKey(JavaClassSource entity)
   {
      for (Member<?> member : entity.getMembers())
      {
         // FORGE-823 Only add entities with @Id as valid entities for REST resource generation.
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
      RestGenerationContext generationContext = createContextFor(uiContext);
      Set<JavaClassSource> endpoints = generateEndpoints(generationContext);
      Project project = generationContext.getProject();
      JavaSourceFacet javaSourceFacet = project.getFacet(JavaSourceFacet.class);
      List<JavaResource> selection = new ArrayList<>();

      for (JavaClassSource javaClass : endpoints)
      {
         selection.add(javaSourceFacet.saveJavaSource(javaClass));
      }
      uiContext.setSelection(selection);
      return Results.success("Endpoint created");
   }

   private Set<JavaClassSource> generateEndpoints(RestGenerationContext generationContext) throws Exception
   {
      RestResourceGenerator selectedGenerator = generator.getValue();
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

   private RestGenerationContext createContextFor(final UIContext context)
   {
      RestGenerationContext generationContext = new RestGenerationContext();
      generationContext.setProject(getSelectedProject(context));
      generationContext.setContentType(Lists.toList(contentType.getValue()));
      generationContext.setPersistenceUnitName(persistenceUnit.getValue());
      generationContext.setTargetPackageName(packageName.getValue());
      generationContext.setInflector(inflector);
      return generationContext;
   }

   @Override
   public NavigationResult getPrerequisiteCommands(UIContext context)
   {
      NavigationResultBuilder builder = NavigationResultBuilder.create();
      Project project = getSelectedProject(context);
      if (project != null)
      {
         if (!project.hasFacet(RestFacet.class))
         {
            builder.add(RestSetupWizard.class);
         }
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
