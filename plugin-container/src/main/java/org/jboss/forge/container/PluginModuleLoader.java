/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.container;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;

import org.jboss.forge.container.exception.ContainerException;
import org.jboss.modules.DependencySpec;
import org.jboss.modules.ModuleIdentifier;
import org.jboss.modules.ModuleLoadException;
import org.jboss.modules.ModuleLoader;
import org.jboss.modules.ModuleSpec;
import org.jboss.modules.ModuleSpec.Builder;
import org.jboss.modules.ResourceLoaderSpec;
import org.jboss.modules.ResourceLoaders;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class PluginModuleLoader extends ModuleLoader
{
   private static final ModuleIdentifier PLUGIN_CONTAINER_API = ModuleIdentifier.create("org.jboss.forge.api:main");
   private static final ModuleIdentifier WELD = ModuleIdentifier.create("org.jboss.weld.core:main");

   @Override
   protected ModuleSpec findModule(ModuleIdentifier id) throws ModuleLoadException
   {
      Builder specBuilder = ModuleSpec.build(id);
      specBuilder.addDependency(DependencySpec.createModuleDependencySpec(PLUGIN_CONTAINER_API));
      specBuilder.addDependency(DependencySpec.createModuleDependencySpec(WELD));

      try
      {
         specBuilder.addResourceRoot(ResourceLoaderSpec.createResourceLoaderSpec(ResourceLoaders
                  .createJarResourceLoader("plugin.jar",
                           new JarFile(new File(
                                    "/Users/lbaxter/.forge/plugins/org/example/plugin/1.0.0-SNAPSHOT/plugin.jar")))));
      }
      catch (IOException e)
      {
         throw new ContainerException("Could not load plugin resource [*TODO*]", e);
      }
      
      return specBuilder.create();
   }

   @Override
   public String toString()
   {
      return "Forge plugin module loader";
   }

}
