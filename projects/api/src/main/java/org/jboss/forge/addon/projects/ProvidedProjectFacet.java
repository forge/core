/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.projects;

import org.jboss.forge.addon.facets.Facet;

/**
 * A {@link ProjectFacet} specifically provided by {@link ProjectProvider} instances. {@link Facet} types implementing this
 * interface will not be installed automatically by the {@link ProjectFactory}.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface ProvidedProjectFacet extends ProjectFacet
{
}
