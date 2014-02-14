/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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
import javax.ws.rs.core.MediaType;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.javaee.ejb.EJBFacet;
import org.jboss.forge.addon.javaee.ejb.ui.EJBSetupWizard;
import org.jboss.forge.addon.javaee.jpa.JPAFacet;
import org.jboss.forge.addon.javaee.jpa.ui.setup.JPASetupWizard;
import org.jboss.forge.addon.javaee.rest.RestFacet;
import org.jboss.forge.addon.javaee.rest.generation.RestResourceGenerator;
import org.jboss.forge.addon.javaee.rest.generator.RestGenerationContextImpl;
import org.jboss.forge.addon.javaee.rest.generator.impl.EntityBasedResourceGenerator;
import org.jboss.forge.addon.javaee.rest.ui.setup.RestSetupWizard;
import org.jboss.forge.addon.javaee.ui.AbstractJavaEECommand;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.text.Inflector;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectMany;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.shrinkwrap.descriptor.api.persistence.PersistenceCommonDescriptor;
import org.jboss.shrinkwrap.descriptor.api.persistence.PersistenceUnitCommon;

/**
 * Generates REST endpoints from JPA Entities
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class RestEndpointFromEntityCommand extends AbstractJavaEECommand
{
   @Inject
   @WithAttributes(label = "Content Type", defaultValue = MediaType.APPLICATION_XML, required = true)
   private UISelectOne<String> contentType;

   @Inject
   @WithAttributes(label = "Targets", required = true)
   private UISelectMany<JavaClass> targets;

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
      JPAFacet<PersistenceCommonDescriptor> persistenceFacet = project.getFacet(JPAFacet.class);
      JavaSourceFacet javaSourceFacet = project.getFacet(JavaSourceFacet.class);
      targets.setValueChoices(persistenceFacet.getAllEntities());
      targets.setItemLabelConverter(new Converter<JavaClass, String>()
      {
         @Override
         public String convert(JavaClass source)
         {
            return source == null ? null : source.getQualifiedName();
         }
      });
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
      packageName.setDefaultValue(javaSourceFacet.getBasePackage() + ".rest");

      contentType.setValueChoices(Arrays.asList(MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON));
      generator.setDefaultValue(defaultResourceGenerator);
      if (context.getProvider().isGUI())
      {
         generator.setItemLabelConverter(new Converter<RestResourceGenerator, String>()
         {
            @Override
            public String convert(RestResourceGenerator source)
            {
               return source == null ? null : source.getDescription();
            }
         });
      }
      else
      {
         generator.setItemLabelConverter(new Converter<RestResourceGenerator, String>()
         {
            @Override
            public String convert(RestResourceGenerator source)
            {
               return source == null ? null : source.getName();
            }
         });
      }
      builder.add(targets)
               .add(generator)
               .add(contentType)
               .add(packageName)
               .add(persistenceUnit)
               .add(overwrite);
   }

   @Override
   public Result execute(final UIExecutionContext context) throws Exception
   {
      UIContext uiContext = context.getUIContext();
      RestGenerationContextImpl generationContext = createContextFor(uiContext);
      Set<JavaClass> endpoints = generateEndpoints(generationContext);
      Project project = generationContext.getProject();
      JavaSourceFacet javaSourceFacet = project.getFacet(JavaSourceFacet.class);
      List<JavaResource> selection = new ArrayList<>();

      for (JavaClass javaClass : endpoints)
      {
         selection.add(javaSourceFacet.saveJavaSource(javaClass));
      }
      uiContext.setSelection(selection);
      return Results.success("Endpoint created");
   }

   private Set<JavaClass> generateEndpoints(RestGenerationContextImpl generationContext) throws Exception
   {
      RestResourceGenerator selectedGenerator = generator.getValue();
      Set<JavaClass> classes = new HashSet<>();
      for (JavaClass target : targets.getValue())
      {
         generationContext.setEntity(target);
         List<JavaClass> artifacts = selectedGenerator.generateFrom(generationContext);
         classes.addAll(artifacts);
      }
      return classes;
   }

   @Override
   protected boolean isProjectRequired()
   {
      return true;
   }

   private RestGenerationContextImpl createContextFor(final UIContext context)
   {
      RestGenerationContextImpl generationContext = new RestGenerationContextImpl();
      generationContext.setProject(getSelectedProject(context));
      generationContext.setContentType(contentType.getValue());
      generationContext.setPersistenceUnitName(persistenceUnit.getValue());
      generationContext.setTargetPackageName(packageName.getValue());
      generationContext.setInflector(inflector);
      return generationContext;
   }

   @Override
   public List<Class<? extends UICommand>> getSetupSteps(UIContext context)
   {
      List<Class<? extends UICommand>> setup = new ArrayList<>();
      Project project = getSelectedProject(context);
      if (!project.hasFacet(RestFacet.class))
      {
         setup.add(RestSetupWizard.class);
      }
      if (!project.hasFacet(JPAFacet.class))
      {
         setup.add(JPASetupWizard.class);
      }
      if (!project.hasFacet(EJBFacet.class))
      {
         setup.add(EJBSetupWizard.class);
      }
      return setup;
   }

}
