/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.maven.projects.archetype.ui;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import javax.inject.Inject;

import org.apache.maven.archetype.catalog.Archetype;
import org.apache.maven.archetype.catalog.ArchetypeCatalog;
import org.jboss.forge.addon.configuration.Configuration;
import org.jboss.forge.addon.configuration.Subset;
import org.jboss.forge.addon.maven.archetype.ArchetypeCatalogFactory;
import org.jboss.forge.addon.maven.archetype.ArchetypeCatalogFactoryRegistry;
import org.jboss.forge.addon.resource.URLResource;
import org.jboss.forge.addon.ui.annotation.Command;
import org.jboss.forge.addon.ui.annotation.Option;
import org.jboss.forge.addon.ui.annotation.predicate.NonGUIEnabledPredicate;
import org.jboss.forge.addon.ui.output.UIOutput;

import com.google.common.base.Strings;

/**
 * Commands for {@link ArchetypeCatalogFactory} registration
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ArchetypeCatalogCommands
{
   @Inject
   @Subset("maven.archetypes")
   private Configuration configuration;

   @Inject
   private ArchetypeCatalogFactoryRegistry archetypeRegistry;

   @Command(value = "Archetype: Add", categories = { "Maven" })
   public void addArchetype(
            @Option(value = "named", label = "Archetype catalog Name", required = true) String name,
            @Option(value = "url", label = "Archetype catalog URL", required = true) URLResource url)
   {
      configuration.setProperty(name, url.getFullyQualifiedName());
      archetypeRegistry.addArchetypeCatalogFactory(name, url.getUnderlyingResourceObject());
   }

   @Command(value = "Archetype: Remove", categories = { "Maven" })
   public void removeArchetype(
            @Option(value = "named", label = "Archetype catalog name", required = true) String name)
   {
      configuration.clearProperty(name);
      archetypeRegistry.removeArchetypeCatalogFactory(name);
   }

   @Command(value = "Archetype: List", categories = { "Maven" }, enabled = NonGUIEnabledPredicate.class)
   public void listArchetypes(@Option(value = "named", label = "Archetype catalog name") String name, UIOutput output)
   {
      PrintStream out = output.out();
      if (Strings.isNullOrEmpty(name))
      {
         Set<String> allKeys = new TreeSet<>();
         Iterator<?> keys = configuration.getKeys();
         while (keys.hasNext())
         {
            String key = keys.next().toString();
            if (!key.isEmpty())
            {
               allKeys.add(key);
            }
         }
         for (ArchetypeCatalogFactory factory : archetypeRegistry.getArchetypeCatalogFactories())
         {
            allKeys.add(factory.getName());
         }
         for (String key : allKeys)
         {
            String catalog = configuration.getString(key, "unknown");
            out.println(key + " = " + catalog);
         }
      }
      else
      {
         ArchetypeCatalogFactory archetypeCatalogFactory = archetypeRegistry.getArchetypeCatalogFactory(name);
         if (archetypeCatalogFactory != null)
         {
            ArchetypeCatalog archetypeCatalog = archetypeCatalogFactory.getArchetypeCatalog();
            if (archetypeCatalog != null)
            {
               for (Archetype archetype : archetypeCatalog.getArchetypes())
               {
                  out.println(archetype);
               }
            }
         }
      }
   }
}
