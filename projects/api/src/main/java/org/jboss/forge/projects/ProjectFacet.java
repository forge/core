/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.projects;

import org.jboss.forge.facets.Facet;

/**
 * A {@link Facet} specifically for use in {@link Project} instances.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface ProjectFacet extends Facet<Project>
{
   /**
    * Set the {@link Project} to which this {@link Facet} belongs. Should only be set once, since each {@link Project}
    * receives its own unique instance of all compatible {@link Facet} types. This method must be called before invoking
    * any operations on {@code this} instance.
    */
   public void setOrigin(Project project);
}
