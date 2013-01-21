/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.project.facets;

import java.util.List;

import org.jboss.forge.project.Facet;
import org.jboss.forge.project.Project;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.FileResource;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface ResourceFacet extends Facet
{
   /**
    * Get a list of {@link DirectoryResource}s representing the directories this project uses to contain {@link Project}
    * non-source documents (such as configuration files.)
    */
   public List<DirectoryResource> getResourceFolders();

   /**
    * Get the {@link DirectoryResource} representing the folder this {@link Project} uses to store package-able,
    * non-source documents (such as configuration files.)
    */
   public DirectoryResource getResourceFolder();

   /**
    * Get the {@link DirectoryResource} representing the folder this {@link Project} uses to store test-scoped
    * non-source documents (such as configuration files.) Files in this directory will never be packaged or deployed
    * except when running Unit Tests.
    */
   public DirectoryResource getTestResourceFolder();

   /**
    * At the given path/filename relative to the project resources directory: {@link #getResourceFolder()} - create a
    * file containing the given bytes.
    * 
    * @return a handle to the {@link FileResource} that was created.
    */
   FileResource<?> createResource(char[] bytes, String relativeFilename);

   /**
    * At the given path/filename relative to the project test resources directory: {@link #getTestResourceFolder()} -
    * create a file containing the given bytes.
    * 
    * @return a handle to the {@link FileResource} that was created.
    */
   FileResource<?> createTestResource(char[] bytes, String relativeFilename);

   /**
    * Return the {@link FileResource} at the given path relative to {@link #getResourceFolder()}. The
    * {@link FileResource} object is returned regardless of whether the target actually exists. To determine if the file
    * exists, you should call {@link FileResource#exists()} on the return value of this method.
    */
   FileResource<?> getResource(String relativePath);

   /**
    * Attempt to locate a {@link FileResource} at the given path relative to {@link #getTestResourceFolder()}. The
    * {@link FileResource} object is returned regardless of whether the target actually exists. To determine if the file
    * exists, you should call {@link FileResource#exists()} on the return value of this method.
    */
   FileResource<?> getTestResource(String relativePath);
}
