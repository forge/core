/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.dependencies;

import org.jboss.forge.furnace.addons.Addon;
import org.jboss.forge.furnace.repositories.AddonRepository;
import org.jboss.forge.furnace.services.Exported;

/**
 * A resolver that knows how to construct {@link DependencyNode} graphs for use in deploying {@link Addon} instances to
 * an {@link AddonRepository}
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Exported
public interface AddonDependencyResolver extends DependencyResolver
{
   /**
    * Resolve the dependency hierarchy for use during {@link Addon} installation.
    */
   public DependencyNode resolveAddonDependencyHierarchy(final DependencyQuery query);

}
