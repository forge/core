/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.projects.facets;

import java.util.List;

import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.visit.ResourceVisitor;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface ResourcesFacet extends ProjectFacet
{
   /**
    * Get a list of {@link DirectoryResource}s representing the directories this project uses to contain {@link Project}
    * non-source documents (such as configuration files.)
    */
   List<DirectoryResource> getResourceDirectories();

   /**
    * Get the {@link DirectoryResource} representing the folder this {@link Project} uses to store package-able,
    * non-source documents (such as configuration files.)
    */
   DirectoryResource getResourceDirectory();

   /**
    * Get the {@link DirectoryResource} representing the folder this {@link Project} uses to store test-scoped
    * non-source documents (such as configuration files.) Files in this directory will never be packaged or deployed
    * except when running Unit Tests.
    */
   DirectoryResource getTestResourceDirectory();

   /**
    * At the given path/filename relative to the project resources directory: {@link #getResourceDirectory()} - create a
    * file containing the given bytes.
    *
    * @return a handle to the {@link FileResource} that was created.
    * @deprecated use {@link ResourcesFacet#getResource(String)} and call {@link FileResource#setContents(char[])}
    */
   @Deprecated
   FileResource<?> createResource(char[] bytes, String relativeFilename);

   /**
    * At the given path/filename relative to the project test resources directory: {@link #getTestResourceDirectory()} -
    * create a file containing the given bytes.
    *
    * @return a handle to the {@link FileResource} that was created.
    * @deprecated use {@link ResourcesFacet#getTestResource(String)} and call {@link FileResource#setContents(char[])}
    */
   @Deprecated
   FileResource<?> createTestResource(char[] bytes, String relativeFilename);

   /**
    * Return the {@link FileResource} at the given path relative to {@link #getResourceDirectory()}. The
    * {@link FileResource} object is returned regardless of whether the target actually exists. To determine if the file
    * exists, you should call {@link FileResource#exists()} on the return value of this method.
    */
   FileResource<?> getResource(String relativePath);

   /**
    * Attempt to locate a {@link FileResource} at the given path relative to {@link #getTestResourceDirectory()}. The
    * {@link FileResource} object is returned regardless of whether the target actually exists. To determine if the file
    * exists, you should call {@link FileResource#exists()} on the return value of this method.
    */
   FileResource<?> getTestResource(String relativePath);

   /**
    * Recursively loops over all the resource directories and for each file it finds, calls the visitor.
    *
    * @param visitor The {@link ResourceVisitor} that processes all the found files. Cannot be null.
    */
   void visitResources(ResourceVisitor visitor);

   /**
    * Recursively loops over all the test resource directories and for each file it finds, calls the visitor.
    *
    * @param visitor The {@link ResourceVisitor} that processes all the found files. Cannot be null.
    */
   void visitTestResources(ResourceVisitor visitor);
}
