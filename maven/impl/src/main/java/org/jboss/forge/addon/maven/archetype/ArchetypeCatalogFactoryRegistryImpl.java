/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.maven.archetype;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.forge.addon.configuration.Configuration;
import org.jboss.forge.addon.configuration.Subset;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.util.Assert;

/**
 * Default implementation for {@link ArchetypeCatalogFactoryRegistry}
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@Singleton
public class ArchetypeCatalogFactoryRegistryImpl implements ArchetypeCatalogFactoryRegistry
{
   private Map<String, ArchetypeCatalogFactory> factories = new TreeMap<>();
   private final Logger log = Logger.getLogger(getClass().getName());

   @Inject
   private AddonRegistry addonRegistry;

   @Inject
   @Subset("maven.archetypes")
   private Configuration archetypeConfiguration;

   /**
    * Add all the available {@link ArchetypeCatalogFactory}es to registry
    */
   @PostConstruct
   void initializeDefaultFactories()
   {
      for (ArchetypeCatalogFactory factory : addonRegistry.getServices(ArchetypeCatalogFactory.class))
      {
         addArchetypeCatalogFactory(factory);
      }
      Iterator<?> keys = archetypeConfiguration.getKeys();
      while (keys.hasNext())
      {
         String name = keys.next().toString();
         if (!name.isEmpty())
         {
            String url = archetypeConfiguration.getString(name);
            try
            {
               URL catalogUrl = new URL(url);
               addArchetypeCatalogFactory(name, catalogUrl);
            }
            catch (MalformedURLException e)
            {
               log.log(Level.SEVERE, "Malformed URL for " + name, e);
            }
         }
      }
   }

   @PreDestroy
   void destroy()
   {
      this.factories.clear();
   }

   @Override
   public void addArchetypeCatalogFactory(String name, URL catalogURL)
   {
      addArchetypeCatalogFactory(new URLArchetypeCatalogFactory(name, catalogURL));
   }

   @Override
   public void addArchetypeCatalogFactory(String name, URL catalogURL, String defaultRepositoryName)
   {
      addArchetypeCatalogFactory(new URLArchetypeCatalogFactory(name, catalogURL, defaultRepositoryName));
   }

   @Override
   public void addArchetypeCatalogFactory(ArchetypeCatalogFactory factory)
   {
      Assert.notNull(factory, "Cannot add a null Archetype Catalog Factory");
      Assert.notNull(factory.getName(), "Archetype Catalog Factory must have a name");
      factories.put(factory.getName(), factory);
   }

   @Override
   public Iterable<ArchetypeCatalogFactory> getArchetypeCatalogFactories()
   {
      return Collections.unmodifiableCollection(factories.values());
   }

   @Override
   public ArchetypeCatalogFactory getArchetypeCatalogFactory(String name)
   {
      return factories.get(name);
   }

   @Override
   public void removeArchetypeCatalogFactory(String name)
   {
      factories.remove(name);
   }

}