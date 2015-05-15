/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.cdi.ui;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.forge.addon.javaee.cdi.CDIFacet;
import org.jboss.forge.addon.javaee.cdi.CDIOperations;
import org.jboss.forge.addon.javaee.ui.AbstractJavaEECommand;
import org.jboss.forge.addon.parser.java.beans.ProjectOperations;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.resource.Resource;
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
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.JavaSource;

/**
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class CDIAddObserverMethodCommand extends AbstractJavaEECommand implements PrerequisiteCommandsProvider
{
   @Inject
   @WithAttributes(label = "Target Class", description = "The class where the method will be created", required = true, type = InputType.DROPDOWN)
   private UISelectOne<JavaResource> targetClass;

   @Inject
   @WithAttributes(label = "Method Name", description = "The name of the created method", required = true)
   private UIInput<String> named;

   @Inject
   @WithAttributes(label = "Event Type", description = "The event type of the created method", type = InputType.JAVA_CLASS_PICKER, required = true)
   private UIInput<String> eventType;

   @Inject
   private ProjectOperations projectOperations;

   @Inject
   private CDIOperations cdiOperations;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      UIContext uiContext = builder.getUIContext();
      Project project = getSelectedProject(uiContext);
      setupTargetClass(uiContext, project);
      setupType();
      builder.add(targetClass).add(named).add(eventType);
   }

   private void setupTargetClass(UIContext uiContext, Project project)
   {
      UISelection<Resource<?>> resource = uiContext.getInitialSelection();
      if (resource.get() instanceof JavaResource)
      {
         targetClass.setDefaultValue((JavaResource) resource.get());
      }
      targetClass.setValueChoices(projectOperations.getProjectClasses(project));
   }

   private void setupType()
   {
      eventType.setCompleter(new UICompleter<String>()
      {
         @Override
         public Iterable<String> getCompletionProposals(final UIContext context, final InputComponent<?, String> input,
                  final String value)
         {
            final Project project = getSelectedProject(context);
            final List<String> options = new ArrayList<>();
            if (project != null)
            {
               for (JavaResource resource : cdiOperations.getProjectEventTypes(project))
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
   protected boolean isProjectRequired()
   {
      return true;
   }

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass()).name("CDI: Add Observer Method")
               .description("Adds a new observer method to a bean")
               .category(Categories.create(Categories.create("Java EE"), "CDI"));
   }

   @Override
   public void validate(UIValidationContext validator)
   {
      JavaResource javaResource = targetClass.getValue();
      if (javaResource != null && javaResource.exists())
      {
         JavaClassSource javaClass;
         try
         {
            javaClass = javaResource.getJavaType();
            if (javaClass.hasMethodSignature(named.getValue(), eventType.getValue()))
            {
               validator.addValidationError(named, "Method signature already exists");
            }
         }
         catch (FileNotFoundException e)
         {
            // ignore
         }
      }
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

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      JavaResource javaResource = targetClass.getValue();
      JavaClassSource javaClass = javaResource.getJavaType();
      javaClass.addMethod().setPublic().setReturnTypeVoid().setName(named.getValue())
               .setBody("")
               .addParameter(eventType.getValue(), "event")
               .addAnnotation(Observes.class);
      javaResource.setContents(javaClass);
      return Results.success();
   }
}
