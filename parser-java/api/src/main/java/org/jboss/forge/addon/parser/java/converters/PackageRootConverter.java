/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.parser.java.converters;

import javax.inject.Inject;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.Projects;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIContextProvider;
import org.jboss.forge.furnace.util.Strings;

/**
 * Converts "~" to the package root name
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class PackageRootConverter implements Converter<String, String>
{
   private final ProjectFactory projectFactory;
   private final UIContextProvider contextProvider;

   @Inject
   public PackageRootConverter(ProjectFactory projectFactory, UIContextProvider contextProvider)
   {
      this.projectFactory = projectFactory;
      this.contextProvider = contextProvider;
   }

   @Override
   public String convert(String source)
   {
      final String result;
      if (Strings.isNullOrEmpty(source) || contextProvider.getUIContext() == null)
      {
         result = source;
      }
      else
      {
         UIContext context = contextProvider.getUIContext();
         Project selectedProject = Projects.getSelectedProject(projectFactory, context);
         if (selectedProject != null && selectedProject.hasFacet(JavaSourceFacet.class))
         {
            String basePackage = selectedProject.getFacet(JavaSourceFacet.class).getBasePackage();
            String fullPackage = source.replaceAll("\\~", basePackage);
            result = fullPackage;
         }
         else
         {
            result = source;
         }
      }
      return result;
   }
}
