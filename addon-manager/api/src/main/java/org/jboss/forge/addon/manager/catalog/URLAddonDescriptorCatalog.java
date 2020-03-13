package org.jboss.forge.addon.manager.catalog;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.jboss.forge.addon.manager.catalog.AddonDescriptor.AddonDescriptorCategory;

/**
 * AbstractAddonDescriptorCatalog
 */
public abstract class URLAddonDescriptorCatalog implements AddonDescriptorCatalog {

    private List<AddonDescriptor> descriptors;

    private URL url;

    public URLAddonDescriptorCatalog(URL url) {
        this.url = url;
    }

    @Override
    public List<AddonDescriptor> getAddonDescriptors() {
       if (descriptors == null) {
          List<AddonDescriptor> found = new ArrayList<>();
          try {
            //  URL url = new URL(url);
             try (InputStream in = url.openStream(); JsonReader reader = Json.createReader(in)) {
                JsonObject obj = reader.readObject();
                // We don't care about core addons as they are already installed
                AddonDescriptorCategory category = AddonDescriptorCategory.COMMUNITY;
                JsonArray addons = obj.getJsonArray(category.name().toLowerCase());
                int size = addons.size();
                for (int idx = 0; idx < size; idx++) {
                   JsonObject addon = addons.getJsonObject(idx);
                   found.add(createAddOnDescriptor(addon));
                }
             }
             this.descriptors = found;
          } catch (Exception e) {
             e.printStackTrace();
             throw new RuntimeException("Error while fetching addons from main catalog", e);
          }
       }
       return descriptors;
    }
 
    protected String[] toArray(JsonArray array) {
       int size = array.size();
       String[] result = new String[size];
       for (int i = 0; i < size; i++) {
          result[i] = array.getString(i);
       }
       return result;
    }

    protected AddonDescriptor createAddOnDescriptor(JsonObject addon) {
        String id = addon.getString("id");
        String name = addon.getString("name");
        String description = addon.getString("description");
        String[] tags = toArray(addon.getJsonArray("tags"));
        String[] installCmd = toArray(addon.getJsonArray("installCmd"));
        String authorName = addon.getJsonObject("author").getString("name");
        return new AddonDescriptorBuilder(id, AddonDescriptorCategory.COMMUNITY)
                    .name(name).description(description)
                    .tags(tags).installCmd(installCmd).authorName(authorName);
     }

     @Override
     public final boolean equals(Object obj) {
        if (this == obj)
           return true;
        if (obj == null)
           return false;
        if (getClass() != obj.getClass())
           return false;

        AddonDescriptorCatalog other = (AddonDescriptorCatalog) obj;
        return other.getName().equals(this.getName());
     }

     @Override
     public String toString() {
        return "AddonDescriptorCatalog [name=" + getName() + "]";
     }


}