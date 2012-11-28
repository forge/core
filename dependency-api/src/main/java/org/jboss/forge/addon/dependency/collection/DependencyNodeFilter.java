/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.dependency.collection;

import org.jboss.forge.addon.dependency.DependencyNode;

/**
 * Used to filter {@link DependencyNode} objects in collections.
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public interface DependencyNodeFilter
{

   /**
    * Return true if the filter accepts this dependency, or false if the dependency should be filtered out.
    */
   boolean accept(DependencyNode dependency);

}
