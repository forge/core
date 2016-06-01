/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.projects.facets;

import java.util.List;

import org.jboss.forge.addon.facets.Facet;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.visit.ResourceVisitor;

/**
 * A {@link Facet} containing APIs to interact with Java Web Projects
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface WebResourcesFacet extends ProjectFacet
{
   /**
    * Get the default Web Root directory (this is the {@link DirectoryResource} containing resources to be deployed to
    * the web-root URL when the application is published. (E.g. In a maven project, files in the
    * <code>/project/src/main/webapp</code> directory are typically published to the root URL:
    * <code>http://localhost:8080/project/</code> root directory. In an eclipse project, this folder is typically
    * located by default at: <code>/project/WebContent/</code>.)
    */
   DirectoryResource getWebRootDirectory();

   /**
    * Get a list containing all possible Web Root {@link DirectoryResource}s for the current project.
    */
   List<DirectoryResource> getWebRootDirectories();

   /**
    * At the given path/filename relative to the project Web Root directory: {@link #getWebRootDirectory()} - create a
    * {@link FileResource} containing the given bytes.
    *
    * @return a handle to the {@link FileResource} that was created.
    * @deprecated use {@link WebResourcesFacet#getWebResource(String)} and call {@link FileResource#setContents(char[])}
    */
   @Deprecated
   FileResource<?> createWebResource(char[] bytes, String relativeFilename);

   /**
    * At the given path/filename relative to the project Web Root directory: {@link #getWebRootDirectory()} - create a
    * {@link FileResource} containing the given String.
    *
    * @return a handle to the {@link FileResource} that was created.
    * @deprecated use {@link WebResourcesFacet#getWebResource(String)} and call {@link FileResource#setContents(String)}
    */
   @Deprecated
   FileResource<?> createWebResource(String render, String relativeFilename);

   /**
    * Get the given {@link FileResource} relative to the project Web Root directory: {@link #getWebRootDirectory()}
    */
   FileResource<?> getWebResource(String relativePath);

   /**
    * Recursively loops over all the resource directories and for each file it finds, calls the visitor.
    *
    * @param visitor The {@link ResourceVisitor} that processes all the found files. Cannot be null.
    */
   void visitWebResources(ResourceVisitor visitor);
}
