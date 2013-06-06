/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.projects.impl.converter;

import java.util.Set;

import javax.inject.Inject;

import org.jboss.forge.addon.convert.AbstractConverter;
import org.jboss.forge.addon.projects.ProjectType;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.services.Exported;
import org.jboss.forge.furnace.services.ExportedInstance;

/**
 * Converts a String to a ProjectType.
 * 
 * TODO: This converter may be useful for other {@link Exported} objects.
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public class ProjectTypeConverter extends AbstractConverter<String, ProjectType>
{

   private final AddonRegistry addonRegistry;

   @Inject
   public ProjectTypeConverter(AddonRegistry addonRegistry)
   {
      super(String.class, ProjectType.class);
      this.addonRegistry = addonRegistry;
   }

   @Override
   public ProjectType convert(String source)
   {
      Set<ExportedInstance<ProjectType>> exportedInstances = addonRegistry.getExportedInstances(ProjectType.class);
      for (ExportedInstance<ProjectType> exportedInstance : exportedInstances)
      {
         ProjectType projectType = null;
         try
         {
            projectType = exportedInstance.get();
            // TODO: This could be reused in other situations
            if (source.equals(projectType.toString()))
            {
               return projectType;
            }
         }
         finally
         {
            exportedInstance.release(projectType);
         }
      }
      return null;
   }
}
