/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.projects.ui;

import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.Projects;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIContextProvider;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.SingleValued;
import org.jboss.forge.addon.ui.input.inject.InputComponentInjectionEnricher;
import org.jboss.forge.addon.ui.input.inject.InputComponentInjectionPoint;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;

/**
 * Sets the default value of the component to the one set in the current project, if any
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class DefaultFacetComponentEnricher implements InputComponentInjectionEnricher
{
   @SuppressWarnings("unchecked")
   @Override
   public void enrich(InputComponentInjectionPoint injectionPoint, InputComponent<?, ?> input)
   {
      UIContextProvider contextProvider = SimpleContainer
               .getServices(getClass().getClassLoader(), UIContextProvider.class).get();
      UIContext context = contextProvider.getUIContext();
      // Setting for Single valued components only at the moment
      if (input instanceof SingleValued && context != null)
      {
         Class<?> valueType = input.getValueType();
         if (ProjectFacet.class.isAssignableFrom(valueType))
         {
            Class<? extends ProjectFacet> projectFacet = (Class<? extends ProjectFacet>) valueType;
            ProjectFactory projectFactory = SimpleContainer
                     .getServices(getClass().getClassLoader(), ProjectFactory.class).get();
            Project project = Projects.getSelectedProject(projectFactory, context);
            if (project != null && project.hasFacet(projectFacet))
            {
               ConverterFactory converterFactory = SimpleContainer
                        .getServices(getClass().getClassLoader(), ConverterFactory.class).get();
               InputComponents.setDefaultValueFor(converterFactory, (InputComponent<?, Object>) input,
                        project.getFacet(projectFacet));
            }
         }
      }
   }
}
