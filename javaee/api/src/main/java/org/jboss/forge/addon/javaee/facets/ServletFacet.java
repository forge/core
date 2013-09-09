/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.facets;

import java.util.List;

import org.jboss.forge.addon.javaee.ConfigurableFacet;
import org.jboss.forge.addon.javaee.JavaEEFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFilter;
import org.jboss.shrinkwrap.descriptor.api.webapp30.WebAppDescriptor;

/**
 * If installed, this {@link Project} supports features from the Servlet specification.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public interface ServletFacet extends JavaEEFacet, ConfigurableFacet<WebAppDescriptor>
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
}
