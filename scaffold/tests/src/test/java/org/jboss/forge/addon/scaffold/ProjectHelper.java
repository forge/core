/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.scaffold;

import org.jboss.forge.addon.parser.java.projects.JavaProjectType;
import org.jboss.forge.addon.parser.java.projects.JavaWebProjectType;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;

/**
 * Helps with the configuration of a project
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ProjectHelper
{

   /**
    * Creates a project installing the required facets from {@link JavaWebProjectType#getRequiredFacets()}
    */
   public Project createWebProject()
   {
      ProjectFactory projectFactory = SimpleContainer.getServices(getClass().getClassLoader(), ProjectFactory.class)
               .get();
      JavaWebProjectType javaWebProjectType = SimpleContainer
               .getServices(getClass().getClassLoader(), JavaWebProjectType.class).get();
      return projectFactory.createTempProject(javaWebProjectType.getRequiredFacets());
   }

   /**
    * Creates a project installing the required facets from {@link JavaProjectType#getRequiredFacets()}
    */
   public Project createJavaLibraryProject()
   {
      ProjectFactory projectFactory = SimpleContainer.getServices(getClass().getClassLoader(), ProjectFactory.class)
               .get();
      JavaProjectType javaProjectType = SimpleContainer
               .getServices(getClass().getClassLoader(), JavaProjectType.class).get();
      return projectFactory.createTempProject(javaProjectType.getRequiredFacets());
   }

}
