/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.archetype;

import java.net.URL;

/**
 * Allows registration of {@link ArchetypeCatalogFactory} objects
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public interface ArchetypeCatalogFactoryRegistry
{
    /**
     * Registers a new {@link ArchetypeCatalogFactory} object
     */
    void addArchetypeCatalogFactory(ArchetypeCatalogFactory factory);

    /**
     * Registers a new {@link ArchetypeCatalogFactory} object
     */
    void addArchetypeCatalogFactory(String name, URL catalogURL);

    /**
     * Registers a new {@link ArchetypeCatalogFactory} object
     */
    void addArchetypeCatalogFactory(String name, URL catalogURL, String defaultRepositoryName);

    /**
     * @return the registered {@link ArchetypeCatalogFactory} objects
     */
    Iterable<ArchetypeCatalogFactory> getArchetypeCatalogFactories();

    /**
     * @return an {@link ArchetypeCatalogFactory} given its name. Null if not
     *         found.
     */
    ArchetypeCatalogFactory getArchetypeCatalogFactory(String name);

    /**
     * Remove an {@link ArchetypeCatalogFactory} given its name.
     */
    void removeArchetypeCatalogFactory(String name);

    /**
     * Return if there are any {@link ArchetypeCatalogFactory} registered
     */
    boolean hasArchetypeCatalogFactories();
}
