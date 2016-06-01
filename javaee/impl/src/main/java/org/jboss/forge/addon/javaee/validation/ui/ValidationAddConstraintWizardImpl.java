/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.validation.ui;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.jboss.forge.addon.javaee.ui.AbstractJavaEECommand;
import org.jboss.forge.addon.javaee.validation.ValidationFacet;
import org.jboss.forge.addon.javaee.validation.ui.setup.ValidationProviderSetupCommandImpl;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.parser.java.resources.JavaResourceVisitor;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.stacks.annotations.StackConstraint;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.visit.VisitContext;
import org.jboss.forge.addon.ui.command.PrerequisiteCommandsProvider;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UINavigationContext;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.result.navigation.NavigationResultBuilder;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.addon.ui.wizard.UIWizard;

@StackConstraint(ValidationFacet.class)
public class ValidationAddConstraintWizardImpl extends AbstractJavaEECommand implements UIWizard,
         PrerequisiteCommandsProvider, ValidationAddConstraintWizard
{
   @Inject
   @WithAttributes(label = "Class", description = "The Java class containing the field", required = true, type = InputType.DROPDOWN)
   private UISelectOne<JavaResource> javaClass;

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass()).name("Constraint: Add")
               .description("Add a Bean Validation constraint")
               .category(Categories.create(super.getMetadata(context).getCategory().getName(), "Bean Validation"));
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      configureClassPicker(builder.getUIContext());
      builder.add(javaClass);
   }

   private void configureClassPicker(UIContext context)
   {
      UISelection<FileResource<?>> selection = context.getInitialSelection();
      Project project = getSelectedProject(context);
      final List<JavaResource> classes = new ArrayList<>();
      if (project != null)
      {
         project.getFacet(JavaSourceFacet.class).visitJavaSources(new JavaResourceVisitor()
         {

            @Override
            public void visit(VisitContext context, JavaResource resource)
            {
               try
               {
                  if (resource.getJavaType().isClass())
                  {
                     classes.add(resource);
                  }
               }
               catch (FileNotFoundException e)
               {
                  // ignore
               }
            }
         });
      }
      javaClass.setValueChoices(classes);
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
         javaClass.setDefaultValue(classes.get(idx));
      }
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      return Results.success();
   }

   @Override
   public NavigationResult next(UINavigationContext context) throws Exception
   {
      JavaResource selectedClass = javaClass.getValue();
      context.getUIContext().getAttributeMap().put(JavaResource.class, selectedClass);
      return Results.navigateTo(ValidationSelectFieldWizardStep.class);
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
         if (!project.hasFacet(ValidationFacet.class))
         {
            builder.add(ValidationProviderSetupCommandImpl.class);
         }
      }
      return builder.build();
   }

}
