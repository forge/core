/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.cdi.ui;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.jboss.forge.addon.javaee.cdi.CDIFacet;
import org.jboss.forge.addon.javaee.cdi.CDIFacet_1_0;
import org.jboss.forge.addon.javaee.cdi.CDIFacet_1_1;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.Projects;
import org.jboss.forge.addon.ui.annotation.Command;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.output.UIOutput;
import org.jboss.shrinkwrap.descriptor.api.beans11.Alternatives;
import org.jboss.shrinkwrap.descriptor.api.beans11.BeansDescriptor;

/**
 * Common CDI commands
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class CDIBeansCommand
{

   @Inject
   private ProjectFactory projectFactory;

   @Command(value = "CDI: List Interceptors", enabled = RequiresCDIFacetPredicate.class, categories = { "Java EE",
            "CDI" })
   public void listInterceptors(final UIContext context, final UIOutput output)
   {
      CDIFacet<?> cdi = getProject(context).getFacet(CDIFacet.class);
      // TODO: Create common descriptor for CDI
      final List<String> interceptors;
      if (cdi instanceof CDIFacet_1_0)
      {
         interceptors = ((CDIFacet_1_0) cdi).getConfig().getOrCreateInterceptors().getAllClazz();
      }
      else
      {
         interceptors = ((CDIFacet_1_1) cdi).getConfig().getOrCreateInterceptors().getAllClazz();
      }
      for (String i : interceptors)
      {
         output.out().println(i);
      }
   }

   @Command(value = "CDI: List Alternatives", enabled = RequiresCDIFacetPredicate.class, categories = { "Java EE",
            "CDI" })
   public void listAlternatives(final UIContext context, final UIOutput output)
   {
      CDIFacet<?> cdi = getProject(context).getFacet(CDIFacet.class);
      // TODO: Create common descriptor for CDI
      final List<String> alternatives;
      if (cdi instanceof CDIFacet_1_0)
      {
         alternatives = ((CDIFacet_1_0) cdi).getConfig().getOrCreateAlternatives().getAllClazz();
      }
      else
      {
         alternatives = new ArrayList<>();
         for (Alternatives<BeansDescriptor> alternativesElement : ((CDIFacet_1_1) cdi).getConfig().getAllAlternatives())
         {
            alternatives.addAll(alternativesElement.getAllClazz());
         }
      }
      for (String i : alternatives)
      {
         output.out().println(i);
      }
   }

   @Command(value = "CDI: List Decorators", enabled = RequiresCDIFacetPredicate.class, categories = { "Java EE", "CDI" })
   public void listDecorators(final UIContext context, final UIOutput output)
   {
      CDIFacet<?> cdi = getProject(context).getFacet(CDIFacet.class);
      // TODO: Create common descriptor for CDI
      final List<String> decorators;
      if (cdi instanceof CDIFacet_1_0)
      {
         decorators = ((CDIFacet_1_0) cdi).getConfig().getOrCreateDecorators().getAllClazz();
      }
      else
      {
         decorators = ((CDIFacet_1_1) cdi).getConfig().getOrCreateDecorators().getAllClazz();
      }
      for (String i : decorators)
      {
         output.out().println(i);
      }
   }

   private Project getProject(UIContext context)
   {
      return Projects.getSelectedProject(projectFactory, context);
   }
}
