package org.jboss.forge.addon.manager.catalog;

import java.util.List;

/**
 * AddonDescriptorCatalogRegistry
 */
public interface AddonDescriptorCatalogRegistry {

    AddonDescriptorCatalogRegistry add(AddonDescriptorCatalog catalog);

    boolean removeByName(String name);

    List<AddonDescriptor> find(String query);
    
}