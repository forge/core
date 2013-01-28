/*
] * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.container.modules;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.jar.JarFile;

import org.jboss.forge.container.AddonDependency;
import org.jboss.forge.container.AddonId;
import org.jboss.forge.container.AddonRepository;
import org.jboss.forge.container.exception.ContainerException;
import org.jboss.forge.container.impl.AddonRepositoryImpl;
import org.jboss.forge.container.modules.providers.ForgeContainerSpec;
import org.jboss.forge.container.modules.providers.SystemClasspathSpec;
import org.jboss.forge.container.modules.providers.WeldClasspathSpec;
import org.jboss.modules.DependencySpec;
import org.jboss.modules.Module;
import org.jboss.modules.ModuleIdentifier;
import org.jboss.modules.ModuleLoadException;
import org.jboss.modules.ModuleLoader;
import org.jboss.modules.ModuleSpec;
import org.jboss.modules.ModuleSpec.Builder;
import org.jboss.modules.ResourceLoaderSpec;
import org.jboss.modules.ResourceLoaders;
import org.jboss.modules.filter.PathFilters;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class AddonModuleLoader extends ModuleLoader
{
   private final Iterable<ModuleSpecProvider> moduleProviders;
   private AddonRepository repository;
   private AddonModuleIdentifierCache moduleCache;

   public AddonModuleLoader(AddonRepository repository, ClassLoader loader)
   {
      this.repository = repository;
      this.moduleCache = new AddonModuleIdentifierCache();

      moduleProviders = ServiceLoader.load(ModuleSpecProvider.class, loader);
      installModuleMBeanServer();
   }

   /**
    * Installs the MBeanServer.
    */
   private void installModuleMBeanServer()
   {
      try
      {
         Method method = ModuleLoader.class.getDeclaredMethod("installMBeanServer");
         method.setAccessible(true);
         method.invoke(null);
      }
      catch (Exception e)
      {
         throw new ContainerException("Could not install Modules MBean server", e);
      }
   }

   @Override
   protected Module preloadModule(ModuleIdentifier identifier) throws ModuleLoadException
   {
      Module pluginModule = super.preloadModule(identifier);
      return pluginModule;
   }

   @Override
   protected ModuleSpec findModule(ModuleIdentifier id) throws ModuleLoadException
   {
      ModuleSpec result = findAddonModule(id);
      if (result == null)
         result = findRegularModule(id);

      return result;
   }

   /**
    * Loads a module based on the {@link AddonId}
    *
    * @param addonId
    * @return
    * @throws ModuleLoadException
    */
   public final Module loadModule(AddonId addonId) throws ModuleLoadException
   {
      try
      {
         return loadModule(moduleCache.getModuleId(addonId));
      }
      catch (ModuleLoadException e)
      {
         e.printStackTrace();
         throw e;
      }
   }

   private ModuleSpec findRegularModule(ModuleIdentifier id)
   {
      ModuleSpec result = null;
      for (ModuleSpecProvider p : moduleProviders)
      {
         result = p.get(this, id);
         if (result != null)
            break;
      }
      return result;
   }

   public ModuleSpec findAddonModule(ModuleIdentifier id)
   {
      AddonId found = findInstalledModule(id);

      if (found != null)
      {
         Builder builder = ModuleSpec.build(id);

         // Set up the ClassPath for this addon Module

         builder.addDependency(DependencySpec.createModuleDependencySpec(SystemClasspathSpec.ID));
         builder.addDependency(DependencySpec.createModuleDependencySpec(PathFilters.acceptAll(),
                  PathFilters.rejectAll(), null, ForgeContainerSpec.ID, false));
         builder.addDependency(DependencySpec.createModuleDependencySpec(PathFilters.acceptAll(),
                  PathFilters.rejectAll(), null, WeldClasspathSpec.ID, false));

         builder.addDependency(DependencySpec.createLocalDependencySpec(PathFilters.acceptAll(),
                  PathFilters.acceptAll()));
         try
         {
            addAddonDependencies(found, builder);
         }
         catch (ContainerException e)
         {
            // TODO implement proper fault handling. For now, abort.
            return null;
         }

         addLocalResources(found, builder);

         return builder.create();
      }
      return null;
   }

   private void addLocalResources(AddonId found, Builder builder)
   {
      List<File> resources = repository.getAddonResources(found);
      for (File file : resources)
      {
         try
         {
            if (file.length() > 0)
               builder.addResourceRoot(
                        ResourceLoaderSpec.createResourceLoaderSpec(
                                 ResourceLoaders.createJarResourceLoader(file.getName(), new JarFile(file)),
                                 PathFilters.acceptAll())
                        );
         }
         catch (IOException e)
         {
            throw new ContainerException("Could not load resources from [" + file.getAbsolutePath() + "]", e);
         }
      }
   }

   private void addAddonDependencies(AddonId found, Builder builder) throws ContainerException
   {
      Set<AddonDependency> addons = repository.getAddonDependencies(found);
      for (AddonDependency dependency : addons)
      {
         ModuleIdentifier moduleId = findCompatibleInstalledModule(dependency);

         if (moduleId == null && !dependency.isOptional())
         {
            throw new ContainerException("Dependency [" + dependency + "] could not be loaded for addon [" + found
                     + "]");
         }
         else
         {
            builder.addDependency(DependencySpec.createModuleDependencySpec(
                     PathFilters.not(PathFilters.getMetaInfFilter()),
                     dependency.isExport() ? PathFilters.acceptAll() : PathFilters.rejectAll(),
                     this,
                     moduleCache.getModuleId(dependency.getId()),
                     dependency.isOptional()));
         }
      }
   }

   private AddonId findInstalledModule(ModuleIdentifier moduleId)
   {
      AddonId found = null;
      for (AddonId addon : repository.listEnabledCompatibleWithVersion(AddonRepositoryImpl.getRuntimeAPIVersion()))
      {
         if (moduleCache.getModuleId(addon).equals(moduleId))
         {
            found = addon;
            break;
         }
      }
      return found;
   }

   private ModuleIdentifier findCompatibleInstalledModule(AddonDependency dependency)
   {
      AddonId found = null;
      for (AddonId addon : repository.listEnabledCompatibleWithVersion(AddonRepositoryImpl.getRuntimeAPIVersion()))
      {
         // TODO implement proper version-range resolution
         if (addon.getName().equals(dependency.getId().getName()))
         {
            found = addon;
            break;
         }
      }

      if (found != null)
      {
         return moduleCache.getModuleId(found);
      }

      return null;
   }

   public AddonRepository getRepository()
   {
      return repository;
   }

   @Override
   public String toString()
   {
      return "AddonModuleLoader";
   }

   public void removeFromCache(AddonId addonId)
   {
      moduleCache.clear(addonId);
   }

}
