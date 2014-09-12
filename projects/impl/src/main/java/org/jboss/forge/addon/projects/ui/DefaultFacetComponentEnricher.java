/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.projects.ui;

import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.Projects;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.InputComponentInjectionEnricher;
import org.jboss.forge.addon.ui.input.SingleValued;
import org.jboss.forge.addon.ui.util.InputComponents;

/**
 * Sets the default value of the component to the one set in the current project, if any
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class DefaultFacetComponentEnricher implements InputComponentInjectionEnricher
{
   @Inject
   private UIContextHandler contextHandler;

   @Inject
   private ConverterFactory converterFactory;

   @Inject
   private ProjectFactory projectFactory;

   @SuppressWarnings("unchecked")
   @Override
   public void enrich(InjectionPoint injectionPoint, InputComponent<?, ?> input)
   {
      UIContext context = contextHandler.getContext();
      // Setting for Single valued components only at the moment
      if (input instanceof SingleValued && context != null)
      {
         Class<?> valueType = input.getValueType();
         if (ProjectFacet.class.isAssignableFrom(valueType))
         {
            Class<? extends ProjectFacet> projectFacet = (Class<? extends ProjectFacet>) valueType;
            Project project = Projects.getSelectedProject(projectFactory, context);
            if (project != null && project.hasFacet(projectFacet))
            {
               InputComponents.setDefaultValueFor(converterFactory, (InputComponent<?, Object>) input,
                        project.getFacet(projectFacet));
            }
         }
      }
   }
}
