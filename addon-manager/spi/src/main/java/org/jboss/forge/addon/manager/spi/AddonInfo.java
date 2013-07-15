/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.manager.spi;

import java.io.File;
import java.util.Set;

import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;

/**
 * Information about an addon
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public interface AddonInfo
{
   /**
    * @return Returns the addon that this object is all about
    */
   public AddonId getAddon();

   /**
    * @return Returns list of required addons from {@link AddonInfo#getAddon()}
    */
   public Set<AddonInfo> getRequiredAddons();

   /**
    * @return Returns list of optional addons from {@link AddonInfo#getAddon()}
    */
   public Set<AddonInfo> getOptionalAddons();

   /**
    * @return the {@link File} resources associated with this addon (Additional dependencies)
    */
   public Set<File> getResources();

   /**
    * Return the dependency entries based on the dependent addons for this {@link AddonInfo}
    */
   public Set<AddonDependencyEntry> getDependencyEntries();
}
