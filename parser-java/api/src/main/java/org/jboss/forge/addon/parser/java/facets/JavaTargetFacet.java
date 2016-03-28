/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.parser.java.facets;

import java.util.List;

import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.resource.DirectoryResource;

/**
 * This facet provides the path to the compiled sources. It will build the project if explicitly installed.
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public interface JavaTargetFacet extends ProjectFacet
{
   /**
    * Get the {@link DirectoryResource} this project uses to contain {@link Project} compiled documents (such as .class
    * files.)
    */
   DirectoryResource getTargetDirectory();

   /**
    * Get the {@link DirectoryResource} this {@link Project} uses to store test-scoped compiled classes (such as .class
    * files.) Files in this directory will never be packaged or deployed except when running Unit Tests.
    */
   DirectoryResource getTestTargetDirectory();

   /**
    * Get a list of {@link DirectoryResource}s this project uses to contain {@link Project} compiled documents (such as
    * .class files.)
    */
   List<DirectoryResource> getTargetDirectories();
}
