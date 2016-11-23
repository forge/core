/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.addons.project;

import java.io.FileNotFoundException;

import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.facets.FacetNotFoundException;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.furnace.addons.Addon;
import org.jboss.forge.furnace.addons.AddonId;

/**
 * Creates Furnace Addon projects
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface AddonProjectConfigurator
{
   /**
    * Create a Furnace {@link Project} with the single project structure.
    */
   public void setupSimpleAddonProject(Project project, Iterable<AddonId> dependencyAddons)
            throws FileNotFoundException, FacetNotFoundException;

   /**
    * Create a Furnace {@link Project} with the full structure (api,impl,tests,spi and addon)
    */
   public void setupComplexAddonProject(Project project, Iterable<AddonId> dependencyAddons)
            throws FileNotFoundException, FacetNotFoundException;

   /**
    * Install the specified {@link Addon} instances into the given {@link Project}.
    */
   public void installSelectedAddons(final Project project, Iterable<AddonId> addons, boolean managed);

   /**
    * Get a {@link Dependency} representing the given {@link AddonId}
    */
   public Dependency toDependency(AddonId addon);

   /**
    * Checks if the {@link Project} depends on the provided {@link AddonId}
    */
   public boolean dependsOnAddon(final Project project, AddonId addonId);

}
