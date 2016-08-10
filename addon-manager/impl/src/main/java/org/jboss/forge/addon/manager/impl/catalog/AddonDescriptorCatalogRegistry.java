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

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public enum AddonDescriptorCatalogRegistry
{
   INSTANCE;

   private List<AddonDescriptorCatalog> catalogs = new ArrayList<>();

   private AddonDescriptorCatalogRegistry()
   {
      add(new ForgeAddonDescriptorCatalog());
   }

   public AddonDescriptorCatalogRegistry add(AddonDescriptorCatalog catalog)
   {
      catalogs.add(catalog);
      return this;
   }

   public AddonDescriptorCatalogRegistry remove(AddonDescriptorCatalog catalog)
   {
      catalogs.remove(catalog);
      return this;
   }

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
}
