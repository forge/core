package org.jboss.forge.addon.manager.impl.catalog;

import java.net.MalformedURLException;
import java.util.logging.Logger;

import org.jboss.forge.addon.manager.catalog.AddonDescriptorCatalogRegistry;
import org.jboss.forge.furnace.container.simple.Producer;

/**
 * AddonDescriptorCatalogRegistryProducer
 */
public class AddonDescriptorCatalogRegistryProducer implements Producer<AddonDescriptorCatalogRegistry> {

    private Logger logger = Logger.getLogger(AddonDescriptorCatalogRegistryProducer.class.getName());

    @Override
    public AddonDescriptorCatalogRegistry get() {
        AddonDescriptorCatalogRegistry registry = new AddonDescriptorCatalogRegistryImpl();

        try {
            registry.add(new ForgeAddonDescriptorCatalog());
        } catch (MalformedURLException e) {
            logger.warning(e.getMessage());
        }

        return registry;
    }   
    
}