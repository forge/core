/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.devtools.java;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.parser.java.beans.ProjectOperations;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.facets.ResourcesFacet;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.facets.HintsFacet;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponentFactory;
import org.jboss.forge.addon.ui.input.UIPrompt;
import org.jboss.forge.addon.ui.input.UISelectMany;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.roaster.model.source.FieldSource;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.util.Refactory;

/**
 * Command generating the Equals and HashCode commands.
 * 
 * @author <a href="mailto:mbriskar@gmail.com">Matej Briškár</a>
 */
@FacetConstraint({ JavaSourceFacet.class, ResourcesFacet.class })
public class JavaEqualsHashcodeCommand extends AbstractProjectCommand
{

   private UISelectOne<JavaResource> targetClass;

   private UISelectMany<String> fields;

   private InputComponentFactory inputFactory;

   private ProjectFactory projectFactory;

   private ProjectOperations projectOperations;

   public JavaEqualsHashcodeCommand()
   {
      Furnace furnace = SimpleContainer.getFurnace(this.getClass().getClassLoader());
      this.inputFactory = furnace.getAddonRegistry().getServices(InputComponentFactory.class).get();
      this.projectFactory = furnace.getAddonRegistry().getServices(ProjectFactory.class).get();
      this.projectOperations = furnace.getAddonRegistry().getServices(ProjectOperations.class).get();
   }

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(getClass()).name("Java: Generate Equals and HashCode")
               .description("Generates equals and hashcode for the given class")
               .category(Categories.create("Java"));
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      UIContext uiContext = builder.getUIContext();
      Project project = getSelectedProject(uiContext);
      targetClass = inputFactory.createSelectOne("targetClass", JavaResource.class);
      targetClass.setDescription("The class where the field will be created");
      targetClass.setRequired(true);
      // Needs to be explicitly set for the Eclipse plugin to recognize. Bug?
      targetClass.getFacet(HintsFacet.class).setInputType(InputType.DROPDOWN);
      UISelection<Object> initialSelection = uiContext.getInitialSelection();
      if (initialSelection.get() instanceof JavaResource)
      {
         targetClass.setValue((JavaResource) initialSelection.get());
      }
      targetClass.setValueChoices(projectOperations.getProjectClasses(project));

      fields = inputFactory.createSelectMany("fields", String.class);
      fields.setDescription("Fields, which should be used in the hashCode/equals method generation");
      fields.setRequired(true).setRequiredMessage("At least one field should be selected");
      
      fields.setValueChoices(new Callable<Iterable<String>>()
      {
         @Override
         public Iterable<String> call() throws Exception
         {
            List<String> strings = new ArrayList<>();
            if (!fields.isEnabled())
            {
               return strings;
            }
            JavaResource javaResource = targetClass.getValue();
            JavaClassSource targetClass = javaResource.getJavaType();
            List<FieldSource<JavaClassSource>> fields = targetClass.getFields();
            for (FieldSource<JavaClassSource> field : fields)
            {
               strings.add(field.getName());
            }
            return strings;
         }
      });
      fields.setEnabled(new Callable<Boolean>()
      {
         @Override
         public Boolean call()
         {
            return (targetClass.getValue() != null);
         }
      });
      builder.add(targetClass).add(fields);
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      JavaResource javaResource = targetClass.getValue();
      JavaClassSource targetClass = javaResource.getJavaType();
      List<FieldSource<JavaClassSource>> selectedFields = new ArrayList<>();
      for (String fieldString : fields.getValue())
      {
         selectedFields.add(targetClass.getField(fieldString));
      }
      UIPrompt prompt = context.getPrompt();
      if (targetClass.hasMethodSignature("equals", Object.class))
      {
         if (prompt.promptBoolean("Class already has an equals method. Would you like it to be overwritten?"))
         {
            Refactory.createEquals(targetClass, selectedFields.toArray(new FieldSource<?>[selectedFields.size()]));
         }
      }
      else
      {
         Refactory.createEquals(targetClass, selectedFields.toArray(new FieldSource<?>[selectedFields.size()]));
      }
      if (targetClass.hasMethodSignature("hashcode"))
      {
         if (prompt.promptBoolean("Class already has a hashcode method. Would you like it to be overwritten?"))
         {
            Refactory.createHashCode(targetClass, selectedFields.toArray(new FieldSource<?>[selectedFields.size()]));
         }
      }
      else
      {
         Refactory.createHashCode(targetClass, selectedFields.toArray(new FieldSource<?>[selectedFields.size()]));
      }
      setCurrentWorkingResource(context, targetClass);
      return Results.success("Command for generation ended successfully");

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

   @Override
   protected ProjectFactory getProjectFactory()
   {
      return projectFactory;
   }
}