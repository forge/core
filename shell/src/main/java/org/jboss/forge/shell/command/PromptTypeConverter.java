/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.command;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.shell.PromptType;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class PromptTypeConverter
{
   private final Instance<Project> projectInstance;

   @Inject
   public PromptTypeConverter(final Instance<Project> projectInstance)
   {
      this.projectInstance = projectInstance;
   }

   public String convert(final PromptType promptType, String value)
   {
      Project project = projectInstance.get();

      if (value != null)
      {
         if (PromptType.JAVA_CLASS.equals(promptType))
         {
            if (value.startsWith("~.") && (project != null))
            {
               if (project.hasFacet(JavaSourceFacet.class))
               {
                  value = value.replaceFirst("~", project.getFacet(JavaSourceFacet.class).getBasePackage());
               }
            }
         }
         else if (PromptType.JAVA_PACKAGE.equals(promptType))
         {
            if (value.startsWith("~.") && (project != null))
            {
               if (project.hasFacet(JavaSourceFacet.class))
               {
                  value = value.replaceFirst("~", project.getFacet(JavaSourceFacet.class).getBasePackage());
               }
            }
         }
      }
      return value;
   }

}
