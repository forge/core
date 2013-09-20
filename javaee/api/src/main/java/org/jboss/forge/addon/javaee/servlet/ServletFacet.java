/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.servlet;

import java.util.List;

import javax.management.Descriptor;

import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.facets.constraints.FacetConstraintType;
import org.jboss.forge.addon.facets.constraints.FacetConstraints;
import org.jboss.forge.addon.javaee.JavaEEFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.facets.WebResourcesFacet;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFilter;
import org.jboss.shrinkwrap.descriptor.api.webapp.WebAppCommonDescriptor;

/**
 * If installed, this {@link Project} supports features from the Servlet specification.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
@SuppressWarnings("rawtypes")
@FacetConstraints({
         @FacetConstraint(value = WebResourcesFacet.class, type = FacetConstraintType.REQUIRED)
})
public interface ServletFacet extends JavaEEFacet
{
   /**
    * List all files in this {@link Project}'s WebContent directory, recursively.
    */
   List<Resource<?>> getResources();

   /**
    * List all files in this {@link Project}'s WebContent directory, recursively, only if they match the given
    * {@link ResourceFilter}.
    */
   List<Resource<?>> getResources(final ResourceFilter filter);

   /**
    * Return a reference to this {@link Project}'s web.xml file.
    */
   public FileResource<?> getConfigFile();

   /**
    * Return the {@link Descriptor} of the specification for which this facet represents.
    */
   WebAppCommonDescriptor getConfig();

}
