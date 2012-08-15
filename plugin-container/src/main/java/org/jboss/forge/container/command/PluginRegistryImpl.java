/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.container.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.forge.plugin.Plugin;
import org.jboss.forge.plugin.meta.PluginMetadata;
import org.jboss.forge.plugin.meta.PluginRegistry;

/**
 * Stores the current registry of all installed & loaded plugins.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Singleton
public class PluginRegistryImpl implements PluginRegistry
{
   private Map<String, List<PluginMetadata>> plugins;
   private Map<String, Map<Class<?>, PluginMetadata>> accessCache;

   private final CommandLibraryExtension library;
   private final BeanManager manager;

   @Inject
   public PluginRegistryImpl(final CommandLibraryExtension library, final BeanManager manager)
   {
      this.library = library;
      this.manager = manager;
   }

   @PostConstruct
   public void init()
   {
      plugins = library.getPlugins();
      accessCache = new HashMap<String, Map<Class<?>, PluginMetadata>>();
   }

   @Override
   public Map<String, List<PluginMetadata>> getPlugins()
   {
      return plugins;
   }

   @Override
   public void addPlugin(final PluginMetadata plugin)
   {
      if (!plugins.containsKey(plugin.getName()))
      {
         plugins.put(plugin.getName(), new ArrayList<PluginMetadata>());
      }

      plugins.get(plugin.getName()).add(plugin);
   }

   @Override
   public String toString()
   {
      return "InstalledPluginRegistry [plugins=" + plugins + "]";
   }

   @Override
   public Plugin instanceOf(final PluginMetadata meta)
   {
      return getContextualInstance(manager, meta.getType());
   }

   @SuppressWarnings("unchecked")
   private static <T> T getContextualInstance(final BeanManager manager, final Class<T> type)
   {
      T result = null;
      Bean<T> bean = (Bean<T>) manager.resolve(manager.getBeans(type));
      if (bean != null)
      {
         CreationalContext<T> context = manager.createCreationalContext(bean);
         if (context != null)
         {
            result = (T) manager.getReference(bean, type, context);
         }
      }
      return result;
   }

   /**
    * Get {@link PluginMetadata} for the plugin with the given name.
    * 
    * @return the metadata, or null if no plugin with given name exists.
    */
   @Override
   public List<PluginMetadata> getPluginMetadata(final String plugin)
   {
      List<PluginMetadata> list = plugins.get(plugin);
      if ((list != null) && !list.isEmpty())
         return Collections.unmodifiableList(list);
      else
         return new ArrayList<PluginMetadata>();
   }

}
