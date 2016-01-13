/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.projects.stacks;

import org.jboss.forge.addon.facets.Facet;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.projects.ProjectType;

/**
 * A {@link StackFacet} represents a set of {@link Facet}s allowed in a project
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public interface StackFacet extends ProjectFacet
{
   /**
    * @return the {@link Stack} bound to this project
    */
   Stack getStack();

   /**
    * Does this {@link StackFacet} support the given {@link ProjectType} in the New Project wizard?
    * 
    * @param projectType the project type chosen in the NewProjectWizard
    * @return <code>true</code> if the stack provided by {@link #getStack()} supports the given {@link ProjectType}
    */
   boolean supports(ProjectType projectType);
}
