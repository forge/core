/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.projects;

import org.jboss.forge.facets.Faceted;
import org.jboss.forge.resource.Resource;

/**
 * Describes a set of {@link Resource} instances that represent a unit of work.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface Project extends Faceted
{
   /**
    * Get a value from this project's internal attributes. If the value is not set, return <code>null</code> instead.
    */
   public Object getAttribute(Object key);

   /**
    * Set a value in this project's internal attributes.
    */
   public void setAttribute(Object key, Object value);

   /**
    * Remove a value from this project's internal attributes.
    */
   public void removeAttribute(Object key);

   /**
    * Get the {@link Resource} representing the root directory of this {@link Project}.
    */
   public Resource<?> getProjectRoot();
}
