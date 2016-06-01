/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.archetype;

import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.maven.archetype.catalog.Archetype;
import org.apache.maven.archetype.catalog.ArchetypeCatalog;
import org.apache.maven.archetype.catalog.io.xpp3.ArchetypeCatalogXpp3Reader;
import org.jboss.forge.furnace.util.Assert;
import org.jboss.forge.furnace.util.Strings;

/**
 * An {@link ArchetypeCatalogFactory} implementation using an {@link URL} as the source
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
class URLArchetypeCatalogFactory implements ArchetypeCatalogFactory
{
   private final Logger logger = Logger.getLogger(getClass().getName());

   private final String name;
   private final URL catalogURL;
   private final String defaultRepository;

   private ArchetypeCatalog cachedArchetypes;

   public URLArchetypeCatalogFactory(String name, URL catalogURL, String defaultRepository)
   {
      super();
      Assert.notNull(name, "Name should not be null");
      Assert.notNull(catalogURL, "Catalog URL must be specified");
      this.name = name;
      this.catalogURL = catalogURL;
      this.defaultRepository = defaultRepository;
   }

   public URLArchetypeCatalogFactory(String name, URL catalogUrl)
   {
      this(name, catalogUrl, extractRepository(catalogUrl));
   }

   private static String extractRepository(URL catalog)
   {
      if (catalog == null)
         return null;
      String url = catalog.toString();
      int idx = url.lastIndexOf('/');
      if (idx == -1)
      {
         return null;
      }
      return url.substring(0, idx);
   }

   @Override
   public String getName()
   {
      return name;
   }

   @Override
   public ArchetypeCatalog getArchetypeCatalog()
   {
      if (cachedArchetypes == null)
      {
         try (InputStream urlStream = catalogURL.openStream())
         {
            cachedArchetypes = new ArchetypeCatalogXpp3Reader().read(urlStream);
            for (Archetype archetype : cachedArchetypes.getArchetypes())
            {
               if (Strings.isNullOrEmpty(archetype.getRepository()))
               {
                  archetype.setRepository(defaultRepository);
               }
            }
         }
         catch (Exception e)
         {
            logger.log(Level.SEVERE, "Error while retrieving archetypes", e);
         }
      }
      return cachedArchetypes;
   }

   @Override
   public String toString()
   {
      return String.valueOf(catalogURL);
   }
}
