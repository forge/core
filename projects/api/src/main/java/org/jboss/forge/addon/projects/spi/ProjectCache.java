/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.projects.spi;

import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.resource.Resource;

/**
 * Responsible for caching {@link Project} instances so that they do not need to be re-built or re-generated for
 * multiple consumers.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface ProjectCache
{
   /**
    * Retrieve a {@link Project} from the cache, using {@link Project#getRootDirectory()} as the key.
    */
   Project get(Resource<?> target);

   /**
    * Invalidate the cache, forcing all stored {@link Project} instances to be re-discovered.
    */
   void invalidate();

   /**
    * Evict the given {@link Project} from this cache instance. If the {@link Project} was not previously cached, this
    * method does nothing.
    */
   void evict(Project project);

   /**
    * Store the given {@link Project} into this cache.
    */
   void store(Project project);

}
