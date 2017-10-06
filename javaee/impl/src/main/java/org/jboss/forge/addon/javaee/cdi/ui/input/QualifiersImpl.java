/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.cdi.ui.input;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.jboss.forge.addon.javaee.cdi.CDIOperations;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.Projects;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.AbstractUIInputManyDecorator;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UICompleter;
import org.jboss.forge.addon.ui.input.UIInputMany;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.furnace.util.Strings;
import org.jboss.forge.roaster.model.source.JavaSource;

/**
 * Implementation of {@link Qualifiers}
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class QualifiersImpl extends AbstractUIInputManyDecorator<String>implements Qualifiers
{
   @Inject
   @WithAttributes(label = "Qualifiers", type = InputType.JAVA_CLASS_PICKER)
   private UIInputMany<String> qualifiers;

   @Inject
   private ProjectFactory projectFactory;

   @Inject
   private CDIOperations cdiOperations;

   @Override
   protected UIInputMany<String> createDelegate()
   {
      qualifiers.setCompleter(new UICompleter<String>()
      {
         @Override
         public Iterable<String> getCompletionProposals(final UIContext context, final InputComponent<?, String> input,
                  final String value)
         {
            final Project project = Projects.getSelectedProject(projectFactory, context);
            final List<String> options = new ArrayList<>();
            for (String type : CDIOperations.DEFAULT_QUALIFIERS)
            {
               if (Strings.isNullOrEmpty(value) || type.startsWith(value))
               {
                  options.add(type);
               }
            }
            if (project != null)
            {
               for (JavaResource resource : cdiOperations.getProjectQualifiers(project))
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
      return qualifiers;
   }
}
