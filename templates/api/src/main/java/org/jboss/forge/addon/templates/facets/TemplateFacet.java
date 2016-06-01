/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.templates.facets;

import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;

/**
 * A Facet for template resource directories. The Facet allows templates from multiple sources/addons (referred to as
 * providers) to be installed in a directory in the project structure.
 * 
 * For instance, templates from the javaee addon could be installed into the 'javaee' sub-directory, while templates
 * from the angularjs addon could be installed into the 'angularjs' sub-directory. The name of the provider is
 * determined by the provider, and only serves as a qualifier to locate templates that may be similarly named. Providers
 * are expected to use unique qualifiers; fully-qualified provider names are recommended.
 * 
 * @author Vineet Reynolds
 */
public interface TemplateFacet extends ProjectFacet
{
   /**
    * Get the {@link org.jboss.forge.addon.resource.DirectoryResource} representing the directory this
    * {@link org.jboss.forge.addon.projects.Project} uses to store templates across all providers.
    */
   public DirectoryResource getRootTemplateDirectory();

   /**
    * Return the {@link org.jboss.forge.addon.resource.FileResource} at the given path relative to
    * {@link #getRootTemplateDirectory()}. The {@link org.jboss.forge.addon.resource.FileResource} object is returned
    * regardless of whether the target actually exists. To determine if the file exists, you should call
    * {@link org.jboss.forge.addon.resource.FileResource#exists()} on the return value of this method.
    * 
    * @param path The path to the template
    */
   FileResource<?> getResource(String path);

}
