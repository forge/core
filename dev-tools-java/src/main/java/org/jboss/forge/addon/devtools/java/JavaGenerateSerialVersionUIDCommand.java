/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.devtools.java;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.parser.java.beans.ProjectOperations;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.input.InputComponentFactory;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectMany;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.roaster.model.source.FieldSource;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.util.SerialVersionUIDComputer;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
@FacetConstraint({ JavaSourceFacet.class })
public class JavaGenerateSerialVersionUIDCommand extends AbstractProjectCommand
{
   private static final String SERIAL_VERSION_FIELD_NAME = "serialVersionUID";

   private UISelectMany<String> targetClasses;
   private UIInput<Boolean> useDefaultValue;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      Project project = getSelectedProject(builder);
      InputComponentFactory inputFactory = builder.getInputComponentFactory();
      Furnace furnace = Furnace.instance(getClass().getClassLoader());
      AddonRegistry addonRegistry = furnace.getAddonRegistry();
      ConverterFactory converterFactory = addonRegistry.getServices(ConverterFactory.class).get();
      ProjectOperations projectOperations = addonRegistry.getServices(ProjectOperations.class).get();

      List<JavaResource> projectClasses = projectOperations.getProjectClasses(project);

      targetClasses = inputFactory.createSelectMany("targetClasses", String.class)
               .setLabel("Target Class")
               .setDescription("The class where the field will be created").setRequired(true)
               .setValueChoices(
                        projectClasses.stream()
                                 .filter(JavaGenerateSerialVersionUIDCommand::isSerializable)
                                 .map(Objects::toString)
                                 .collect(Collectors.toList()));

      UIContext uiContext = builder.getUIContext();
      UISelection<Object> initialSelection = uiContext.getInitialSelection();

      if (initialSelection.get() instanceof JavaResource)
      {
         InputComponents.setValueFor(converterFactory, targetClasses,
                  ((JavaResource) initialSelection.get()).getJavaType().getQualifiedName());
      }

      useDefaultValue = inputFactory.createInput("useDefaultValue", Boolean.class)
               .setLabel("Generate Default Value?")
               .setDescription("If true, sets the serialVersionUID field to 1L");

      builder.add(targetClasses).add(useDefaultValue);
   }

   private static boolean isSerializable(JavaResource resource)
   {
      try
      {
         JavaClassSource javaType = (JavaClassSource) resource.getJavaType();
         return javaType.hasInterface(Serializable.class);
      }
      catch (FileNotFoundException e)
      {
         return false;
      }
   }

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(getClass()).name("Java: Generate SerialVersionUID")
               .description("Generates the serialVersionUID field for the given class")
               .category(Categories.create("Java"));
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      Project project = getSelectedProject(context);
      JavaSourceFacet sourceFacet = project.getFacet(JavaSourceFacet.class);
      for (String targetClass : targetClasses.getValue())
      {
         JavaResource javaResource = sourceFacet.getJavaResource(targetClass);
         JavaClassSource javaType = javaResource.getJavaType();
         FieldSource<JavaClassSource> serialVersionUID = getOrCreateSerialVersionUID(javaType);
         if (useDefaultValue.getValue())
         {
            serialVersionUID.setLiteralInitializer("1L");
         }
         else
         {
            // Calculate it
            long value = SerialVersionUIDComputer.compute(javaType);
            serialVersionUID.setLiteralInitializer(value + "L");
         }
         javaResource.setContents(javaType);
      }
      return Results.success();
   }

   /**
    * @param javaType
    * @return
    */
   private FieldSource<JavaClassSource> getOrCreateSerialVersionUID(JavaClassSource javaType)
   {
      FieldSource<JavaClassSource> field = javaType.getField(SERIAL_VERSION_FIELD_NAME);
      if (field == null)
      {
         // private static final long serialVersionUID = 1L;
         field = javaType.addField().setPrivate().setStatic(true).setFinal(true).setType(long.class)
                  .setName(SERIAL_VERSION_FIELD_NAME);
      }
      return field;
   }

   @Override
   protected boolean isProjectRequired()
   {
      return true;
   }

   @Override
   protected ProjectFactory getProjectFactory()
   {
      Furnace furnace = Furnace.instance(getClass().getClassLoader());
      AddonRegistry addonRegistry = furnace.getAddonRegistry();
      return addonRegistry.getServices(ProjectFactory.class).get();
   }

}
