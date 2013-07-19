/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.maven.addon;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.manager.spi.AddonInfo;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;

/**
 * Information about an addon
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public class AddonInfoBuilder implements AddonInfo
{
   private final AddonId addon;

   private final Map<AddonInfo, Boolean> requiredAddons = new HashMap<AddonInfo, Boolean>();
   private final Map<AddonInfo, Boolean> optionalAddons = new HashMap<AddonInfo, Boolean>();
   private final Set<File> resources = new HashSet<File>();

   private AddonInfoBuilder(AddonId addon)
   {
      this.addon = addon;
   }

   public static AddonInfoBuilder from(AddonId addonId)
   {
      AddonInfoBuilder builder = new AddonInfoBuilder(addonId);
      return builder;
   }

   public AddonInfoBuilder addRequiredDependency(AddonInfo addonInfo, boolean exported)
   {
      requiredAddons.put(addonInfo, exported);
      return this;
   }

   public AddonInfoBuilder addOptionalDependency(AddonInfo addonInfo, boolean exported)
   {
      optionalAddons.put(addonInfo, exported);
      return this;
   }

   public AddonInfoBuilder addResource(File file)
   {
      resources.add(file);
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
      return Collections.unmodifiableSet(optionalAddons.keySet());
   }

   /**
    * Returns an unmodifiable list of the required addons
    */
   @Override
   public Set<AddonInfo> getRequiredAddons()
   {
      return Collections.unmodifiableSet(requiredAddons.keySet());
   }

   @Override
   public Set<File> getResources()
   {
      return Collections.unmodifiableSet(resources);
   }

   @Override
   public Set<AddonDependencyEntry> getDependencyEntries()
   {
      Set<AddonDependencyEntry> entries = new HashSet<AddonDependencyEntry>();
      for (Entry<AddonInfo, Boolean> entry : requiredAddons.entrySet())
      {
         AddonId key = entry.getKey().getAddon();
         Boolean exported = entry.getValue();
         entries.add(AddonDependencyEntry.create(key.getName(), key.getVersion().toString(), exported, false));
      }
      for (Entry<AddonInfo, Boolean> entry : optionalAddons.entrySet())
      {
         AddonId key = entry.getKey().getAddon();
         Boolean exported = entry.getValue();
         entries.add(AddonDependencyEntry.create(key.getName(), key.getVersion().toString(), exported, true));
      }
      return entries;
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
