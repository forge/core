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

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.forge.addon.javaee.cdi.CDIOperations;
import org.jboss.forge.addon.javaee.cdi.ui.input.Qualifiers;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UICompleter;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.util.Strings;
import org.jboss.forge.roaster.model.Visibility;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.JavaSource;
import org.jboss.forge.roaster.model.source.ParameterSource;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class CDIAddObserverMethodCommand extends AbstractMethodCDICommand
{
   @Inject
   @WithAttributes(label = "Event Type", description = "The event type of the created method", type = InputType.JAVA_CLASS_PICKER, required = true)
   private UIInput<String> eventType;

   @Inject
   private Qualifiers qualifiers;

   @Inject
   private CDIOperations cdiOperations;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      super.initializeUI(builder);
      setupType();
      builder.add(eventType).add(qualifiers);
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
   public Metadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass()).name("CDI: Add Observer Method")
               .description("Adds a new observer method to a bean");
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      JavaResource javaResource = targetClass.getValue();
      JavaClassSource javaClass = javaResource.getJavaType();
      ParameterSource<JavaClassSource> parameter = javaClass.addMethod().setPublic().setReturnTypeVoid()
               .setName(named.getValue())
               .setBody("")
               .addParameter(eventType.getValue(), "event");
      parameter.addAnnotation(Observes.class);
      for (String qualifier : qualifiers.getValue())
      {
         parameter.addAnnotation(qualifier);
      }
      javaResource.setContents(javaClass);
      return Results.success();
   }


   @Override
   protected Visibility getDefaultVisibility()
   {
      return Visibility.PRIVATE;
   }
}
