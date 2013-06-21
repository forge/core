/*
] * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.furnace.modules;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.logging.Logger;

import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.exception.ContainerException;
import org.jboss.forge.furnace.impl.AddonRepositoryImpl;
import org.jboss.forge.furnace.modules.providers.FurnaceContainerSpec;
import org.jboss.forge.furnace.modules.providers.SystemClasspathSpec;
import org.jboss.forge.furnace.modules.providers.WeldClasspathSpec;
import org.jboss.forge.furnace.modules.providers.XPathJDKClasspathSpec;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.furnace.repositories.AddonRepository;
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
   private static final Logger logger = Logger.getLogger(AddonModuleLoader.class.getName());

   private Furnace forge;
   private final Iterable<ModuleSpecProvider> moduleProviders;

   private AddonModuleIdentifierCache moduleCache;
   private AddonModuleJarFileCache moduleJarFileCache;

   public AddonModuleLoader(Furnace forge)
   {
      this.forge = forge;
      this.moduleCache = new AddonModuleIdentifierCache();
      this.moduleJarFileCache = new AddonModuleJarFileCache();
      moduleProviders = ServiceLoader.load(ModuleSpecProvider.class, forge.getRuntimeClassLoader());
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
    */
   public final Module loadModule(AddonId addonId) throws ModuleLoadException
   {
      try
      {
         Module result = loadModule(moduleCache.getModuleId(addonId));
         return result;
      }
      catch (ModuleLoadException e)
      {
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
      for (AddonRepository repository : forge.getRepositories())
      {
         AddonId found = findInstalledModule(repository, id);

         if (found != null)
         {
            Builder builder = ModuleSpec.build(id);

            // Set up the ClassPath for this addon Module

            builder.addDependency(DependencySpec.createModuleDependencySpec(SystemClasspathSpec.ID));
            builder.addDependency(DependencySpec.createModuleDependencySpec(XPathJDKClasspathSpec.ID));
            builder.addDependency(DependencySpec.createModuleDependencySpec(PathFilters.acceptAll(),
                     PathFilters.rejectAll(), null, FurnaceContainerSpec.ID, false));
            builder.addDependency(DependencySpec.createModuleDependencySpec(PathFilters.acceptAll(),
                     PathFilters.rejectAll(), null, WeldClasspathSpec.ID, false));

            builder.addDependency(DependencySpec.createLocalDependencySpec(PathFilters.acceptAll(),
                     PathFilters.acceptAll()));
            try
            {
               addAddonDependencies(repository, found, builder);
            }
            catch (ContainerException e)
            {
               // TODO implement proper fault handling. For now, abort.
               logger.warning(e.getMessage());
               return null;
            }

            addLocalResources(repository, found, builder, id);

            return builder.create();
         }
      }
      return null;
   }

   private void addLocalResources(AddonRepository repository, AddonId found, Builder builder, ModuleIdentifier id)
   {
      List<File> resources = repository.getAddonResources(found);
      for (File file : resources)
      {
         try
         {
            if (file.isDirectory())
            {
               builder.addResourceRoot(
                        ResourceLoaderSpec.createResourceLoaderSpec(
                                 ResourceLoaders.createFileResourceLoader(file.getName(), file),
                                 PathFilters.acceptAll())
                        );
            }
            else if (file.length() > 0)
            {
               JarFile jarFile = new JarFile(file);
               moduleJarFileCache.addJarFileReference(id, jarFile);
               builder.addResourceRoot(
                        ResourceLoaderSpec.createResourceLoaderSpec(
                                 ResourceLoaders.createJarResourceLoader(file.getName(), jarFile),
                                 PathFilters.acceptAll())
                        );
            }
         }
         catch (IOException e)
         {
            throw new ContainerException("Could not load resources from [" + file.getAbsolutePath() + "]", e);
         }
      }
   }

   private void addAddonDependencies(AddonRepository repository, AddonId found, Builder builder)
            throws ContainerException
   {
      Set<AddonDependencyEntry> addons = repository.getAddonDependencies(found);
      for (AddonDependencyEntry dependency : addons)
      {
         ModuleIdentifier moduleId = findCompatibleInstalledModule(dependency.getId());

         if (moduleId == null && !dependency.isOptional())
         {
            throw new ContainerException("Dependency [" + dependency + "] could not be loaded for addon [" + found
                     + "]");
         }
         else
         {
            builder.addDependency(DependencySpec.createModuleDependencySpec(
                     PathFilters.not(PathFilters.getMetaInfFilter()),
                     dependency.isExported() ? PathFilters.acceptAll() : PathFilters.rejectAll(),
                     this,
                     moduleCache.getModuleId(dependency.getId()),
                     dependency.isOptional()));
         }
      }
   }

   private AddonId findInstalledModule(AddonRepository repository, ModuleIdentifier moduleId)
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

   private ModuleIdentifier findCompatibleInstalledModule(AddonId addonId)
   {
      AddonId found = null;

      for (AddonRepository repository : forge.getRepositories())
      {
         for (AddonId addon : repository.listEnabledCompatibleWithVersion(AddonRepositoryImpl.getRuntimeAPIVersion()))
         {
            // TODO implement proper version-range resolution
            if (addon.getName().equals(addonId.getName()))
            {
               found = addon;
               break;
            }
         }
      }

      if (found != null)
      {
         return moduleCache.getModuleId(found);
      }

      return null;
   }

   @Override
   public String toString()
   {
      return "AddonModuleLoader";
   }

   public void releaseAddonModule(AddonId addonId)
   {
      ModuleIdentifier id = moduleCache.getModuleId(addonId);
      moduleJarFileCache.closeJarFileReferences(id);
      moduleCache.clear(addonId);
   }

}
