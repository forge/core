/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.scaffoldx.facets;

import java.io.InputStream;

import org.jboss.forge.project.Facet;
import org.jboss.forge.project.Project;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.FileResource;

public interface ScaffoldTemplateFacet extends Facet
{
   /**
    * Get the {@link DirectoryResource} representing the directory this {@link Project} uses to store templates across
    * all scaffold providers.
    */
   public DirectoryResource getRootTemplateDirectory();

   /**
    * Get the {@link DirectoryResource} representing the directory this {@link Project} uses to store templates for the
    * provided scaffold provider.
    */
   public DirectoryResource getTemplateDirectory(String provider);

   /**
    * At the given path/filename relative to the scaffold provider specific directory under the project templates
    * directory: {@link #getTemplateDirectory()} - create a file containing the given bytes.
    * 
    * The provider name cannot be null.
    * 
    * @return a handle to the {@link FileResource} that was created.
    */
   FileResource<?> createResource(InputStream data, String provider, String relativeFilename);

   /**
    * At the given path/filename relative to the scaffold provider specific directory under the project templates
    * directory: {@link #getTemplateDirectory()} - create a file containing the given bytes.
    * 
    * The provider name cannot be null.
    * 
    * @return a handle to the {@link FileResource} that was created.
    */
   FileResource<?> createResource(char[] data, String provider, String relativeFilename);

   /**
    * At the given path/filename relative to the scaffold provider specific directory under the project templates
    * directory: {@link #getTemplateDirectory()} - create a file containing the given bytes.
    * 
    * The provider name cannot be null.
    * 
    * @return a handle to the {@link FileResource} that was created.
    */
   FileResource<?> createResource(String data, String provider, String relativeFilename);

   /**
    * Return the {@link FileResource} at the given path relative to {@link #getTemplateDirectory()}. The
    * {@link FileResource} object is returned regardless of whether the target actually exists. To determine if the file
    * exists, you should call {@link FileResource#exists()} on the return value of this method.
    */
   FileResource<?> getResource(String provider, String relativePath);

}
