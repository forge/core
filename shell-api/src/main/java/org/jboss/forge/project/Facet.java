/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.project;

import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.shell.plugins.RequiresPackagingType;

/**
 * Represents a standardized piece of a project on which certain {@link Plugin} types may rely for a set of
 * domain-specific operations. A {@link Facet} is an access point to common functionality, file manipulations,
 * descriptors that extend a {@link Project} instance. When implementing this interface, consider extending
 * {@link BaseFacet} for convenience.
 * <p>
 * Facets may be annotated with any of the following constraints in order to ensure proper dependencies are satisfied at
 * runtime: {@link RequiresFacet}, {@link RequiresPackagingType}
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @see {@link BaseFacet}
 */
public interface Facet
{
   /**
    * Return the {@link Project} instance on which this {@link Facet} operates.
    */
   Project getProject();

   /**
    * Initialize this {@link Facet} for operation on the given {@link Project}. This method is responsible for ensuring
    * that the {@link Facet} instance is ready for use, and must be called before any other methods.
    */
   void setProject(Project project);

   /**
    * Perform necessary setup for this {@link Facet} to be considered installed in the given {@link Project}. This
    * method should NOT register the facet; facet registration is handled by the project if installation is successful.
    * 
    * @return true if installation was successful, false if not.
    */
   boolean install();

   /**
    * Return true if the {@link Facet} is available for the given {@link Project}, false if otherwise.
    */
   boolean isInstalled();

   /**
    * Remove this {@link Facet} from its {@link Project}, and perform any necessary cleanup.
    */
   boolean uninstall();
}
