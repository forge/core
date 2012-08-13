/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.loader;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;

import org.jboss.modules.DependencySpec;
import org.jboss.modules.Module;
import org.jboss.modules.ModuleIdentifier;
import org.jboss.modules.ModuleLoadException;
import org.jboss.modules.ModuleLoader;
import org.jboss.modules.ModuleSpec;
import org.jboss.modules.ModuleSpec.Builder;
import org.jboss.modules.ResourceLoaderSpec;
import org.jboss.modules.ResourceLoaders;

/**
 * To use this loader, just instantiate it and start loading modules. it will handle delegation to the boot module
 * loader.
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 *
 *         http://docs.jboss.org/jbossmodules/1.0.2.GA/api/index.html?org/jboss/modules/ModuleSpec.html
 */
public class ModularPluginLoader extends ModuleLoader
{
   @Override
   protected Module preloadModule(final ModuleIdentifier identifier) throws ModuleLoadException
   {
      /*
       * This method is used only to determine which module loader a module belongs to. Calling preloadModule() from
       * that loader specifies that the module should be loaded from that loader.
       */

      if (!identifier.equals("belongs to me"))
      {
         ModuleLoader loader = Module.getBootModuleLoader();
         return preloadModule(identifier, loader); // get the 'identifier' module from the boot loader instead of ours
      }

      /*
       * need some kind of criteria that determines whether or not a module is a plugin, or whether it should actually
       * be loaded by the system module loader - see: ClassifyingModuleLoader
       */

      return super.preloadModule(identifier); // this is mine,
   }

   @Override
   protected ModuleSpec findModule(final ModuleIdentifier id) throws ModuleLoadException
   {
      try
      {
         id.getName();
         id.getSlot();
         Builder builder = ModuleSpec.build(id);
         builder.addResourceRoot(ResourceLoaderSpec.createResourceLoaderSpec(ResourceLoaders.createJarResourceLoader(
                  "name-of-jar", new JarFile(new File("path to jar")))));
         builder.addDependency(DependencySpec.createLocalDependencySpec()); // adds the classes from this module itself
         builder.addDependency(DependencySpec.createModuleDependencySpec(ModuleIdentifier.create("org.jboss.forge")));
         return builder.create();
      }
      catch (IOException e)
      {
         throw new ModuleLoadException("Failed to load module", e);
      }
   }

   @Override
   public String toString()
   {
      return "ModularPluginLoader";
   }

}
