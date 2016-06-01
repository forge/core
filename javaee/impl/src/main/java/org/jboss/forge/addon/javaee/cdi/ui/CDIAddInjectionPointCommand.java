/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.cdi.ui;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.jboss.forge.addon.javaee.cdi.CDIFacet;
import org.jboss.forge.addon.javaee.cdi.CDIOperations;
import org.jboss.forge.addon.javaee.cdi.ui.input.Qualifiers;
import org.jboss.forge.addon.javaee.ui.AbstractJavaEECommand;
import org.jboss.forge.addon.parser.java.beans.FieldOperations;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.ui.command.PrerequisiteCommandsProvider;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UICompleter;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UIPrompt;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.result.navigation.NavigationResultBuilder;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.util.Strings;
import org.jboss.forge.roaster.model.Visibility;
import org.jboss.forge.roaster.model.source.FieldSource;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.JavaSource;

/**
 * Adds a CDI injection point
 *
 * @author <a href="antonio.goncalves@gmail.com">Antonio Goncalves</a>
 */
public class CDIAddInjectionPointCommand extends AbstractJavaEECommand implements PrerequisiteCommandsProvider
{
   @Inject
   @WithAttributes(label = "Field Name", description = "The field name to be injected in the target bean", required = true)
   private UIInput<String> named;

   @Inject
   @WithAttributes(label = "Target Class", description = "The class where the injection point will be added", required = true, type = InputType.DROPDOWN)
   private UISelectOne<JavaResource> targetClass;

   @Inject
   @WithAttributes(label = "Field Type", description = "The type intended to be used for this field", type = InputType.JAVA_CLASS_PICKER, required = true)
   private UIInput<String> type;

   @Inject
   private Qualifiers qualifiers;

   @Inject
   private FieldOperations beanOperations;

   @Inject
   private CDIOperations cdiOperations;

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass()).name("CDI: Add Injection Point")
               .description("Adds a new injection point field to a bean")
               .category(Categories.create(Categories.create("Java EE"), "CDI"));
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      setupTargetClasses(builder.getUIContext());
      setupType();
      builder.add(targetClass).add(named).add(type).add(qualifiers);
   }

   private void setupTargetClasses(UIContext uiContext)
   {

      UISelection<FileResource<?>> selection = uiContext.getInitialSelection();
      Project project = getSelectedProject(uiContext);
      final List<JavaResource> classes = cdiOperations.getProjectInjectionPointBeans(project);
      targetClass.setValueChoices(classes);
      int idx = -1;
      if (!selection.isEmpty())
      {
         idx = classes.indexOf(selection.get());
      }
      if (idx == -1)
      {
         idx = classes.size() - 1;
      }
      if (idx != -1)
      {
         targetClass.setDefaultValue(classes.get(idx));
      }
   }

   private void setupType()
   {
      type.setCompleter(new UICompleter<String>()
      {
         @Override
         public Iterable<String> getCompletionProposals(final UIContext context, final InputComponent<?, String> input,
                  final String value)
         {
            final Project project = getSelectedProject(context);
            final List<String> options = new ArrayList<>();
            if (project != null)
            {
               for (JavaResource resource : cdiOperations.getProjectInjectableBeans(project))
               {
                  try
                  {
                     JavaSource<?> javaSource = resource.getJavaType();
                     String qualifiedName = javaSource.getQualifiedName();
                     if (Strings.isNullOrEmpty(value) || qualifiedName.startsWith(value))
                     {
                        options.add(qualifiedName);
                     }
                  }
                  catch (FileNotFoundException ignored)
                  {
                  }
               }
            }
            return options;
         }
      });
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      JavaResource javaResource = targetClass.getValue();
      String fieldNameStr = named.getValue();
      JavaClassSource targetBean = javaResource.getJavaType();
      FieldSource<JavaClassSource> field = targetBean.getField(fieldNameStr);
      if (field != null)
      {
         UIPrompt prompt = context.getPrompt();
         if (prompt.promptBoolean("Field '" + field.getName() + "' already exists. Do you want to overwrite it?"))
         {
            beanOperations.removeField(targetBean, field);
         }
         else
         {
            return Results.fail("Field '" + field.getName() + "' already exists.");
         }
      }

      FieldSource<?> injectionPoint = targetBean.addField().setName(fieldNameStr).setVisibility(Visibility.PRIVATE)
               .setType(type.getValue());
      injectionPoint.addAnnotation(Inject.class);

      for (String qualifier : qualifiers.getValue())
      {
         injectionPoint.addAnnotation(qualifier);
      }

      javaResource.setContents(targetBean);
      return Results.success("Injection point " + fieldNameStr + " was added.");
   }

   @Override
   public void validate(UIValidationContext validator)
   {
      super.validate(validator);
      try
      {
         JavaResource javaResource = targetClass.getValue();
         if (javaResource != null)
         {
            JavaClassSource javaClass = javaResource.getJavaType();
            if (javaClass.hasField(named.getValue()))
            {
               validator.addValidationWarning(targetClass, "Field '" + named.getValue() + "' already exists");
            }
         }
      }
      catch (FileNotFoundException ffe)
      {
         validator.addValidationError(targetClass, "Bean could not be found");
      }
   }

   @Override
   protected boolean isProjectRequired()
   {
      return true;
   }

   @Override
   public NavigationResult getPrerequisiteCommands(UIContext context)
   {
      NavigationResultBuilder builder = NavigationResultBuilder.create();
      Project project = getSelectedProject(context);
      if (project != null)
      {
         if (!project.hasFacet(CDIFacet.class))
         {
            builder.add(CDISetupCommand.class);
         }
      }
      return builder.build();
   }
}
