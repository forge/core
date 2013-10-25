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
import org.jboss.forge.addon.javaee.jpa.PersistenceFacet;
import org.jboss.forge.addon.javaee.rest.generation.RestResourceGenerator;
import org.jboss.forge.addon.javaee.rest.generator.RestGenerationContextImpl;
import org.jboss.forge.addon.javaee.rest.generator.impl.EntityBasedResourceGenerator;
import org.jboss.forge.addon.javaee.ui.AbstractJavaEECommand;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.text.Inflector;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectMany;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.addon.ui.wizard.UIWizard;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.shrinkwrap.descriptor.api.persistence20.PersistenceDescriptor;
import org.jboss.shrinkwrap.descriptor.api.persistence20.PersistenceUnit;

/**
 * Generates REST endpoints from JPA Entities
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class RestEndpointFromEntityWizard extends AbstractJavaEECommand implements UIWizard
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
      return Metadata.from(super.getMetadata(context), getClass()).name("REST: Endpoint From Entity")
               .description("Generate REST endpoints from JPA entities")
               .category(Categories.create(super.getMetadata(context).getCategory(), "JAX-RS"));
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      UIContext context = builder.getUIContext();
      Project project = getSelectedProject(context);
      PersistenceFacet persistenceFacet = project.getFacet(PersistenceFacet.class);
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
      List<String> persistenceUnits = new ArrayList<String>();
      for (PersistenceUnit<PersistenceDescriptor> persistenceUnit : persistenceFacet.getConfig()
               .getAllPersistenceUnit())
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
   public Result execute(final UIContext context) throws Exception
   {
      RestGenerationContextImpl generationContext = createContextFor(context);
      Set<JavaClass> endpoints = generateEndpoints(generationContext);
      Project project = generationContext.getProject();
      JavaSourceFacet javaSourceFacet = project.getFacet(JavaSourceFacet.class);
      List<JavaResource> selection = new ArrayList<JavaResource>();

      for (JavaClass javaClass : endpoints)
      {
         selection.add(javaSourceFacet.saveJavaSource(javaClass));
      }
      context.setSelection(selection);
      return Results.success("Endpoint created");
   }

   private Set<JavaClass> generateEndpoints(RestGenerationContextImpl generationContext) throws Exception
   {
      RestResourceGenerator selectedGenerator = generator.getValue();
      Set<JavaClass> classes = new HashSet<JavaClass>();
      for (JavaClass target : targets.getValue())
      {
         generationContext.setEntity(target);
         List<JavaClass> artifacts = selectedGenerator.generateFrom(generationContext);
         classes.addAll(artifacts);
      }
      return classes;
   }

   @Override
   public void validate(final UIValidationContext validator)
   {
      super.validate(validator);
   }

   @Override
   public boolean isEnabled(final UIContext context)
   {
      boolean enabled;
      if (super.isEnabled(context))
      {
         Project project = getSelectedProject(context);
         enabled = project.hasFacet(PersistenceFacet.class) && project.hasFacet(JavaSourceFacet.class);
      }
      else
      {
         enabled = false;
      }
      return enabled;
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
      // generationContext.setEntity(entity);
      generationContext.setPersistenceUnitName(persistenceUnit.getValue());
      generationContext.setTargetPackageName(packageName.getValue());
      generationContext.setInflector(inflector);
      return generationContext;
   }

   @Override
   public NavigationResult next(UIContext context) throws Exception
   {
      Project project = getSelectedProject(context);
      if (project.hasFacet(EJBFacet.class))
      {
         return null;
      }
      else
      {
         return Results.navigateTo(EJBSetupWizard.class);
      }
   }

}
