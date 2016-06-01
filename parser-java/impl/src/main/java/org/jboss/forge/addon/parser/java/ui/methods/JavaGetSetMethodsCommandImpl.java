/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.parser.java.ui.methods;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.parser.java.beans.ProjectOperations;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UIPrompt;
import org.jboss.forge.addon.ui.input.UISelectMany;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.util.Strings;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.MethodSource;
import org.jboss.forge.roaster.model.source.PropertySource;

@FacetConstraint(JavaSourceFacet.class)
public class JavaGetSetMethodsCommandImpl extends AbstractProjectCommand implements JavaGetSetMethodsCommand
{
   @Inject
   @WithAttributes(label = "Target Class", description = "The class where the field will be created", required = true, type = InputType.DROPDOWN)
   private UISelectOne<JavaResource> targetClass;

   @Inject
   @WithAttributes(label = "Use builder pattern", description = "Use builder pattern for generating accessor and mutator methods", defaultValue = "false")
   private UIInput<Boolean> builderPattern;

   @Inject
   @WithAttributes(label = "Java properties", description = "Properties, for which the get/set methods should be generated", required = true)
   private UISelectMany<String> properties;

   @Inject
   private ProjectOperations projectOperations;

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(getClass()).name("Java: Generate Getters and Setters")
               .description("Generates mutators and accessors for the given class")
               .category(Categories.create("Java"));
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      setupTargetClass(builder.getUIContext());
      properties.setValueChoices(new Callable<Iterable<String>>()
      {
         @Override
         public Iterable<String> call() throws Exception
         {
            List<String> strings = new ArrayList<>();
            JavaResource javaResource = targetClass.getValue();
            JavaClassSource targetClass = javaResource.getJavaType();
            List<PropertySource<JavaClassSource>> properties = targetClass.getProperties();
            for (PropertySource<JavaClassSource> property : properties)
            {
               strings.add(property.getName());
            }
            return strings;
         }
      });
      properties.setEnabled(new Callable<Boolean>()
      {
         @Override
         public Boolean call()
         {
            return (targetClass.getValue() != null);
         }
      });
      builder.add(targetClass).add(builderPattern).add(properties);
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      JavaResource javaResource = targetClass.getValue();
      JavaClassSource targetClass = javaResource.getJavaType();
      GetSetMethodGenerator generator;
      if (builderPattern.getValue())
      {
         generator = new BuilderGetSetMethodGenerator();
      }
      else
      {
         generator = new DefaultGetSetMethodGenerator();
      }
      List<PropertySource<JavaClassSource>> selectedProperties = new ArrayList<>();
      if (properties == null || properties.getValue() == null)
      {
         return Results.fail("No properties were selected");
      }
      for (String selectedProperty : properties.getValue())
      {
         selectedProperties.add(targetClass.getProperty(selectedProperty));
      }

      for (PropertySource<JavaClassSource> property : selectedProperties)
      {
         MethodSource<JavaClassSource> accessor = targetClass
                  .getMethod("get" + Strings.capitalize(property.getName()));
         if (accessor == null)
         {
            generator.createAccessor(property);
         }
         else
         {
            if (!generator.isCorrectAccessor(accessor, property))
            {
               if (promptToFixMethod(context, accessor.getName(), property.getName()))
               {
                  targetClass.removeMethod(accessor);
                  generator.createMutator(property);
               }
            }
         }
         String mutatorMethodName = "set" + Strings.capitalize(property.getName());
         String mutatorMethodParameter = property.getType().getName();
         MethodSource<JavaClassSource> mutator = targetClass.getMethod(mutatorMethodName, mutatorMethodParameter);
         if (mutator == null)
         {
            generator.createMutator(property);
         }
         else
         {
            if (!generator.isCorrectMutator(mutator, property))
            {
               if (promptToFixMethod(context, mutator.getName(), property.getName()))
               {
                  targetClass.removeMethod(mutator);
                  generator.createMutator(property);
               }
            }
         }
      }
      setCurrentWorkingResource(context, targetClass);
      return Results.success("Mutators and accessors were generated successfully");

   }

   private boolean promptToFixMethod(UIExecutionContext context, String methodName, String propertyName)
   {
      UIPrompt prompt = context.getPrompt();
      return prompt.promptBoolean("Method '" + methodName + "'already exists for property"
               + propertyName + " . Method is not following the selected pattern."
               + " Should it be fixed?");

   }

   private void setupTargetClass(UIContext context)
   {
      UISelection<FileResource<?>> selection = context.getInitialSelection();
      Project project = getSelectedProject(context);
      final List<JavaResource> entities = projectOperations.getProjectClasses(project);
      targetClass.setValueChoices(entities);
      int idx = -1;
      if (!selection.isEmpty())
      {
         idx = entities.indexOf(selection.get());
      }
      if (idx != -1)
      {
         targetClass.setDefaultValue(entities.get(idx));
      }
   }

   private void setCurrentWorkingResource(UIExecutionContext context, JavaClassSource javaClass)
            throws FileNotFoundException
   {
      Project selectedProject = getSelectedProject(context);
      if (selectedProject != null)
      {
         JavaSourceFacet facet = selectedProject.getFacet(JavaSourceFacet.class);
         facet.saveJavaSource(javaClass);
      }
      context.getUIContext().setSelection(javaClass);
   }

   @Override
   protected boolean isProjectRequired()
   {
      return true;
   }

   @Inject
   private ProjectFactory projectFactory;

   @Override
   protected ProjectFactory getProjectFactory()
   {
      return projectFactory;
   }
}