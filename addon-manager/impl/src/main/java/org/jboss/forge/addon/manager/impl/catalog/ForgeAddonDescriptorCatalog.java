/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.manager.impl.catalog;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.jboss.forge.addon.manager.impl.catalog.AddonDescriptor.AddonDescriptorCategory;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class ForgeAddonDescriptorCatalog implements AddonDescriptorCatalog
{
   private List<AddonDescriptor> descriptors;

   @Override
   public List<AddonDescriptor> getAddonDescriptors()
   {
      if (descriptors == null)
      {
         List<AddonDescriptor> found = new ArrayList<>();
         try
         {
            URL url = new URL("https://forge.jboss.org/api/addons?source=cmd");
            try (InputStream in = url.openStream();
                     JsonReader reader = Json.createReader(in))
            {
               JsonObject obj = reader.readObject();
               // We don't care about core addons as they are already installed
               AddonDescriptorCategory category = AddonDescriptorCategory.COMMUNITY;
               JsonArray addons = obj.getJsonArray(category.name().toLowerCase());
               int size = addons.size();
               for (int idx = 0; idx < size; idx++)
               {
                  JsonObject addon = addons.getJsonObject(idx);

                  String id = addon.getString("id");
                  String name = addon.getString("name");
                  String description = addon.getString("description");
                  String[] tags = toArray(addon.getJsonArray("tags"));
                  String[] installCmd = toArray(addon.getJsonArray("installCmd"));
                  String authorName = addon.getJsonObject("author").getString("name");
                  found.add(new AddonDescriptorBuilder(id, category)
                           .name(name)
                           .description(description)
                           .tags(tags)
                           .installCmd(installCmd)
                           .authorName(authorName));
               }
            }
            this.descriptors = found;
         }
         catch (Exception e)
         {
            e.printStackTrace();
            throw new RuntimeException("Error while fetching addons from main catalog", e);
         }
      }
      return descriptors;
   }

   private String[] toArray(JsonArray array)
   {
      int size = array.size();
      String[] result = new String[size];
      for (int i = 0; i < size; i++)
      {
         result[i] = array.getString(i);
      }
      return result;
   }
}
