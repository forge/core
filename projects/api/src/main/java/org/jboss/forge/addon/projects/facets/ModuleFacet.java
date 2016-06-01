/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.projects.facets;

import java.util.List;

import org.jboss.forge.addon.facets.Facet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFacet;

/**
 * A {@link Facet} to be used when the project contains modules
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public interface ModuleFacet extends ProjectFacet
{
   /**
    * The list of modules in this {@link Project}
    */
   List<String> getModules();
}
