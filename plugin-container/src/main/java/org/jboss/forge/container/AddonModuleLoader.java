/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.container;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;

import org.jboss.forge.container.AddonRegistry.AddonEntry;
import org.jboss.forge.container.exception.ContainerException;
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
 * @author <a href="mailto:gegastaldi@gmail.com">George Gastaldi</a>
 */
public class AddonModuleLoader extends ModuleLoader
{
   private static final ModuleIdentifier PLUGIN_CONTAINER_API = ModuleIdentifier.create("org.jboss.forge.api");
   private static final ModuleIdentifier PLUGIN_CONTAINER = ModuleIdentifier.create("org.jboss.forge");
   private static final ModuleIdentifier WELD = ModuleIdentifier.create("org.jboss.weld");

   private Map<String, AddonEntry> installed;
   private ModuleLoader parent;

   public AddonModuleLoader(List<AddonEntry> installedAddons)
   {
      this.installed = new HashMap<String, AddonRegistry.AddonEntry>();
      for (AddonEntry entry : installedAddons)
      {
         this.installed.put(entry.toModuleId(), entry);
      }
      this.parent = Module.getBootModuleLoader();
   }

   @Override
   protected Module preloadModule(ModuleIdentifier identifier) throws ModuleLoadException
   {
      if (findModule(identifier) != null)
      {
         Module pluginModule = super.preloadModule(identifier);
         return pluginModule;
      }
      else
         return preloadModule(identifier, parent);
   }

   @Override
   protected ModuleSpec findModule(ModuleIdentifier id) throws ModuleLoadException
   {
      AddonEntry found = installed.get(id.toString());
      if (found != null)
      {
         Builder builder = ModuleSpec.build(id);

         builder.addDependency(DependencySpec.createLocalDependencySpec());
         builder.addDependency(DependencySpec.createModuleDependencySpec(PathFilters.acceptAll(),
                  PathFilters.rejectAll(), parent, PLUGIN_CONTAINER_API, false));
         builder.addDependency(DependencySpec.createModuleDependencySpec(PathFilters.acceptAll(),
                  PathFilters.rejectAll(), parent, PLUGIN_CONTAINER, false));
         builder.addDependency(DependencySpec.createModuleDependencySpec(PathFilters.acceptAll(),
                  PathFilters.rejectAll(), parent, WELD, false));

         List<File> jars = AddonRegistry.getPluginResourceJars(found);

         for (File jarFile : jars)
         {
            try
            {
               builder.addResourceRoot(
                        ResourceLoaderSpec.createResourceLoaderSpec(
                                 ResourceLoaders.createJarResourceLoader(jarFile.getName(), new JarFile(jarFile, true)),
                                 PathFilters.acceptAll())
                        );
            }
            catch (IOException e)
            {
               throw new ContainerException("Could not load plugin resource [" + jarFile.getAbsolutePath() + "]", e);
            }
         }

         return builder.create();
      }
      return null;
   }

   @Override
   public String toString()
   {
      return "AddonModuleLoader";
   }

}
