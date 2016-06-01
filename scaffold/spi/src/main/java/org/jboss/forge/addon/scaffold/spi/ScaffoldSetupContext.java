/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.scaffold.spi;

import org.jboss.forge.addon.projects.Project;

/**
 * A context object for the scaffold
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ScaffoldSetupContext
{
   private final String targetDirectory;
   private final Project project;

   public ScaffoldSetupContext(String targetDirectory, Project project)
   {
      super();
      this.targetDirectory = targetDirectory == null ? "" : targetDirectory;
      this.project = project;
   }

   public String getTargetDirectory()
   {
      return targetDirectory;
   }

   public Project getProject()
   {
      return project;
   }
}
