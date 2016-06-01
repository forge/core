/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.archetype;

import org.apache.maven.archetype.catalog.ArchetypeCatalog;

/**
 * Provides a list of catalogs to be used
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public interface ArchetypeCatalogFactory
{
   /**
    * Returns the name of this {@link ArchetypeCatalogFactory}
    */
   String getName();

   /**
    * Returns the {@link ArchetypeCatalog} object represented by this catalog
    */
   ArchetypeCatalog getArchetypeCatalog();
}
