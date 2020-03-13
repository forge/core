/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.manager.impl.catalog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jboss.forge.addon.manager.catalog.AddonDescriptor;
import org.jboss.forge.addon.manager.catalog.AddonDescriptorCatalog;
import org.jboss.forge.addon.manager.catalog.AddonDescriptorCatalogRegistry;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class AddonDescriptorCatalogRegistryImpl implements AddonDescriptorCatalogRegistry {

   private List<AddonDescriptorCatalog> catalogs = new ArrayList<>();

   public AddonDescriptorCatalogRegistry add(AddonDescriptorCatalog catalog) {
      catalogs.add(catalog);
      return this;
   }

   @Override
   public boolean removeByName(String name) {
      return catalogs.removeIf(t -> t.getName().equalsIgnoreCase(name));
   }

   @Override
   public List<AddonDescriptor> find(String query)
   {
      List<AddonDescriptor> result = new ArrayList<>();
      for (AddonDescriptorCatalog catalog : catalogs)
      {
         catalog.getAddonDescriptors().stream()
                  .filter(d -> d.getName().toLowerCase().contains(query.toLowerCase())
                           || d.getDescription().toLowerCase().contains(query.toLowerCase())
                           || Arrays.stream(d.getTags()).anyMatch(t -> query.toLowerCase().contains(t.toLowerCase())))
                  .forEach(result::add);
      }
      return result;
   }

   @Override
   public String toString() {
      return "AddonDescriptorCatalogRegistryImpl [catalogs=" + catalogs + "]";
   }
   
}
