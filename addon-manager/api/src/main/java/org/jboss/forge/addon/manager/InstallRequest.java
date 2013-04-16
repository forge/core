/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.manager;

import java.util.List;

import org.jboss.forge.container.addons.Addon;
import org.jboss.forge.container.repositories.AddonRepository;
import org.jboss.forge.dependencies.DependencyNode;

/**
 * When an addon is installed, another addons could be required. This object returns the necessary information for the
 * installation of an addon to succeed, like required addons and dependencies
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public interface InstallRequest
{

   /**
    * The requested addon
    * 
    * @return
    */
   public abstract DependencyNode getRequestedAddon();

   /**
    * Returns an unmodifiable list of the required addons
    */
   public abstract List<DependencyNode> getOptionalAddons();

   /**
    * Returns an unmodifiable list of the required addons
    */
   public abstract List<DependencyNode> getRequiredAddons();

   /**
    * This will deploy all the required {@link Addon} 
    */
   public abstract void perform();

   /**
    * This will deploy all the required addons to the specified {@link AddonRepository}
    */
   public abstract void perform(AddonRepository target);

}