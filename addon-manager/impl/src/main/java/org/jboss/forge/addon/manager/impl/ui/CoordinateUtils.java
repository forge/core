/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.manager.impl.ui;

import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.manager.spi.AddonDependencyResolver;
import org.jboss.forge.furnace.versions.SingleVersion;
import org.jboss.forge.furnace.versions.Version;
import org.jboss.forge.furnace.versions.Versions;

public class CoordinateUtils
{
   static final String FORGE_ADDON_GROUP_ID = "org.jboss.forge.addon:";

   public static AddonId resolveCoordinate(String addonCoordinates, Version specificationVersion,
            AddonDependencyResolver resolver) throws IllegalArgumentException
   {
      AddonId addon;
      // This allows forge --install maven
      if (addonCoordinates.contains(","))
      {
         if (addonCoordinates.contains(":"))
         {
            addon = AddonId.fromCoordinates(addonCoordinates);
         }
         else
         {
            addon = AddonId.fromCoordinates(FORGE_ADDON_GROUP_ID + addonCoordinates);
         }
      }
      else
      {
         AddonId[] versions;
         String coordinate;
         if (addonCoordinates.contains(":"))
         {
            coordinate = addonCoordinates;
            versions = resolver.resolveVersions(addonCoordinates).get();
         }
         else
         {
            coordinate = FORGE_ADDON_GROUP_ID + addonCoordinates;
            versions = resolver.resolveVersions(coordinate).get();
         }

         if (versions.length == 0)
         {
            throw new IllegalArgumentException("No Artifact version found for " + coordinate);
         }
         else
         {
            AddonId selected = null;
            for (int i = versions.length - 1; selected == null && i >= 0; i--)
            {
               String apiVersion = resolver.resolveAPIVersion(versions[i]).get();
               if (apiVersion != null
                        && Versions.isApiCompatible(specificationVersion, SingleVersion.valueOf(apiVersion)))
               {
                  selected = versions[i];
               }
            }
            if (selected != null)
            {
               addon = selected;
            }
            else
            {
               addon = versions[versions.length - 1];
            }

         }
      }
      return addon;
   }
}
