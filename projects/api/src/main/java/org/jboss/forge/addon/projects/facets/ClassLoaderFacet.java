/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.projects.facets;

import java.net.URLClassLoader;
import java.util.List;

import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.resource.FileResource;

/**
 * A {@link ProjectFacet} with the {@link Project}'s {@link ClassLoader}
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public interface ClassLoaderFacet extends ProjectFacet
{
   /**
    * Returns a {@link URLClassLoader} that encompasses all {@link Dependency} instances on which this project depends.
    * It also includes the compiled/fully built {@link PackagingFacet#getFinalArtifact()} from the project sources
    * itself. This is the equivalent of class-loading the entire project classpath.
    * <p/>
    * WARNING: You *MUST* call {@link URLClassLoader#close()} when finished with this object. Failure to close this
    * object upon completion will result in fatal memory leaks over time. If the scope of work is appropriate, consider
    * using a try-with-resources block to encapsulate the operations and automatically clean up any ClassLoader
    * resources.
    * <p/>
    * IMPORTANT: You must also clean up and release any {@link Class} references that were produced by this
    * {@link ClassLoader}. It is not enough to close this. Held {@link Class} references will keep the
    * {@link ClassLoader} from being garbage collected.
    */
   URLClassLoader getClassLoader();

   /**
    * Returns a {@link URLClassLoader} that encompasses all {@link Dependency} instances on which this project depends.
    * It also includes the compiled/fully built {@link List} of {@link FileResource} objects. This is the equivalent of
    * class-loading the entire project classpath. This method will not attempt to build the project
    * <p/>
    * WARNING: You *MUST* call {@link URLClassLoader#close()} when finished with this object. Failure to close this
    * object upon completion will result in fatal memory leaks over time. If the scope of work is appropriate, consider
    * using a try-with-resources block to encapsulate the operations and automatically clean up any ClassLoader
    * resources.
    * <p/>
    * IMPORTANT: You must also clean up and release any {@link Class} references that were produced by this
    * {@link ClassLoader}. It is not enough to close this. Held {@link Class} references will keep the
    * {@link ClassLoader} from being garbage collected.
    */
   URLClassLoader getClassLoader(List<? extends FileResource<?>> compiledResources);

}
