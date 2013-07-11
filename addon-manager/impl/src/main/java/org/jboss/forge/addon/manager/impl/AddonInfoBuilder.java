/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.manager.impl;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.jboss.forge.addon.dependencies.DependencyNode;
import org.jboss.forge.addon.manager.AddonInfo;
import org.jboss.forge.furnace.addons.AddonId;

/**
 * Information about an addon
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public class AddonInfoBuilder implements AddonInfo
{
   private final AddonId addon;
   private final DependencyNode dependencyNode;

   private final Set<AddonInfo> requiredAddons = new HashSet<AddonInfo>();
   private final Set<AddonInfo> optionalAddons = new HashSet<AddonInfo>();
   private final Set<File> resources = new HashSet<File>();

   private AddonInfoBuilder(AddonId addon, DependencyNode dependencyNode)
   {
      this.addon = addon;
      this.dependencyNode = dependencyNode;
   }

   public static AddonInfoBuilder from(AddonId addonId, DependencyNode dependencyNode)
   {
      AddonInfoBuilder builder = new AddonInfoBuilder(addonId, dependencyNode);
      return builder;
   }

   public AddonInfoBuilder addOptionalDependency(AddonInfo addonInfo)
   {
      optionalAddons.add(addonInfo);
      return this;
   }

   public AddonInfoBuilder addResource(File file)
   {
      resources.add(file);
      return this;
   }

   public AddonInfoBuilder addRequiredDependency(AddonInfo addonInfo)
   {
      requiredAddons.add(addonInfo);
      return this;
   }

   @Override
   public AddonId getAddon()
   {
      return addon;
   }

   /**
    * Returns an unmodifiable list of the required addons
    */
   @Override
   public Set<AddonInfo> getOptionalAddons()
   {
      return Collections.unmodifiableSet(optionalAddons);
   }

   /**
    * Returns an unmodifiable list of the required addons
    */
   @Override
   public Set<AddonInfo> getRequiredAddons()
   {
      return Collections.unmodifiableSet(requiredAddons);
   }

   @Override
   public Set<File> getResources()
   {
      return Collections.unmodifiableSet(resources);
   }

   /**
    * This method is used internally for performance reasons
    */
   @Override
   public DependencyNode getDependencyNode()
   {
      return dependencyNode;
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((addon == null) ? 0 : addon.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (!(obj instanceof AddonInfo))
         return false;
      AddonInfo other = (AddonInfo) obj;
      if (addon == null)
      {
         if (other.getAddon() != null)
            return false;
      }
      else if (!addon.equals(other.getAddon()))
         return false;
      return true;
   }

   @Override
   public String toString()
   {
      return addon.toString();
   }
}
