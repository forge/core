/*
] * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.container.modules;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.ServiceLoader;
import java.util.jar.JarFile;

import org.jboss.forge.container.AddonEntry;
import org.jboss.forge.container.AddonUtil;
import org.jboss.forge.container.AddonUtil.AddonDependency;
import org.jboss.forge.container.exception.ContainerException;
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
 * TODO See {@link JarModuleLoader} for how to do dynamic dependencies from an XML file within.
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 *
 */
public class AddonModuleLoader extends ModuleLoader
{
   Iterable<ModuleSpecProvider> moduleProviders = ServiceLoader.load(ModuleSpecProvider.class);

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
         result = findDependencyModule(id);

      return result;
   }

   private ModuleSpec findDependencyModule(ModuleIdentifier id)
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
      AddonEntry found = findInstalledModule(id);

      if (found != null)
      {
         Builder builder = ModuleSpec.build(id);

         // Set up the ClassPath for this addon Module
         builder.addDependency(DependencySpec.createModuleDependencySpec(SystemClasspathSpec.ID));

         builder.addDependency(DependencySpec.createModuleDependencySpec(PathFilters.acceptAll(),
                  PathFilters.rejectAll(), null, WeldClasspathSpec.ID, false));

         builder.addDependency(DependencySpec.createModuleDependencySpec(PathFilters.acceptAll(),
                  PathFilters.rejectAll(), null, ForgeContainerSpec.ID, false));

         builder.addDependency(DependencySpec.createLocalDependencySpec(PathFilters.acceptAll(),
                  PathFilters.acceptAll()));

         try
         {
            // TODO increment service counter here?
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

   private void addLocalResources(AddonEntry found, Builder builder)
   {
      List<File> resources = AddonUtil.getAddonResources(found);
      for (File file : resources)
      {
         try
         {
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

   private void addAddonDependencies(AddonEntry found, Builder builder) throws ContainerException
   {
      List<AddonDependency> addons = AddonUtil.getAddonDependencies(found);
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
            builder.addDependency(DependencySpec.createModuleDependencySpec(PathFilters.acceptAll(),
                     PathFilters.rejectAll(), this, moduleId, dependency.isOptional()));
         }
      }
   }

   private AddonEntry findInstalledModule(ModuleIdentifier moduleId)
   {
      AddonEntry found = null;
      for (AddonEntry addon : AddonUtil.listByAPICompatibleVersion("2.0.0-SNAPSHOT"))
      {
         if (addon.toModuleId().equals(moduleId.toString()))
         {
            found = addon;
            break;
         }
      }
      return found;
   }

   private ModuleIdentifier findCompatibleInstalledModule(AddonDependency dependency)
   {
      AddonEntry found = null;
      for (AddonEntry addon : AddonUtil.listByAPICompatibleVersion("2.0.0-SNAPSHOT"))
      {
         // TODO implement proper version-range resolution
         if (addon.getName().equals(dependency.getName()))
         {
            found = addon;
            break;
         }
      }

      if (found != null)
         return ModuleIdentifier.create(found.getName(), found.getSlot());

      return null;
   }

   @Override
   public String toString()
   {
      return "AddonModuleLoader";
   }

}
