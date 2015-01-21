/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.projects.facets;

import java.net.URL;
import java.net.URLClassLoader;

import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFacet;

/**
 * A {@link ProjectFacet} with the {@link Project}'s {@link ClassLoader}
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public interface ClassLoaderFacet extends ProjectFacet
{
   /**
    * Returns the {@link URLClassLoader} with the {@link URL}s that this project depends on.
    * 
    * WARNING: Remember to close this {@link URLClassLoader} when no longer in use.
    * <p/>
    * Leaving it open could introduce memory leaks.
    */
   URLClassLoader getClassLoader();

}
