/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

import org.jboss.forge.addon.configuration.Configuration;
import org.jboss.forge.furnace.container.simple.AbstractEventListener;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.furnace.services.Imported;
import org.jboss.forge.furnace.util.Assert;

/**
 * Default implementation for {@link ArchetypeCatalogFactoryRegistry}
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ArchetypeCatalogFactoryRegistryImpl extends AbstractEventListener
         implements ArchetypeCatalogFactoryRegistry
{
   private Map<String, ArchetypeCatalogFactory> factories = new TreeMap<>();
   private final Logger log = Logger.getLogger(getClass().getName());

   private Imported<ArchetypeCatalogFactory> services;

   private Configuration getArchetypeConfiguration()
   {
      return SimpleContainer.getServices(getClass().getClassLoader(), Configuration.class).get()
               .subset("maven.archetypes");
   }

   /**
    * Registers the {@link ArchetypeCatalogFactory} objects from the user {@link Configuration}
    */
   @Override
   protected void handleThisPostStartup()
   {
      services = SimpleContainer.getServices(getClass().getClassLoader(), ArchetypeCatalogFactory.class);
      Configuration archetypeConfiguration = getArchetypeConfiguration();
      Iterator<?> keys = archetypeConfiguration.getKeys();
      while (keys.hasNext())
      {
         String name = keys.next().toString();
         if (!name.isEmpty())
         {
            String url = archetypeConfiguration.getString(name);
            try
            {
               addArchetypeCatalogFactory(name, new URL(url));
            }
            catch (MalformedURLException e)
            {
               log.log(Level.SEVERE, "Malformed URL for " + name, e);
            }
         }
      }
   }

   @Override
   protected void handleThisPreShutdown()
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
      Map<String, ArchetypeCatalogFactory> result = new TreeMap<>();
      for (ArchetypeCatalogFactory factory : services)
      {
         result.put(factory.getName(), factory);
      }
      result.putAll(factories);
      return Collections.unmodifiableCollection(result.values());
   }

   @Override
   public ArchetypeCatalogFactory getArchetypeCatalogFactory(String name)
   {
      ArchetypeCatalogFactory result = null;
      if (name != null)
      {
         for (ArchetypeCatalogFactory factory : getArchetypeCatalogFactories())
         {
            if (name.equals(factory.getName()))
               return factory;
         }
      }
      return result;
   }

   @Override
   public void removeArchetypeCatalogFactory(String name)
   {
      factories.remove(name);
   }

   @Override
   public boolean hasArchetypeCatalogFactories()
   {
      return factories.size() > 0 || !services.isUnsatisfied();
   }

}
