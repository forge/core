package org.jboss.forge.container.modules.providers;

import java.util.Set;

import org.jboss.forge.container.modules.ModuleSpecProvider;
import org.jboss.modules.DependencySpec;
import org.jboss.modules.ModuleIdentifier;
import org.jboss.modules.ModuleLoader;
import org.jboss.modules.ModuleSpec;
import org.jboss.modules.ModuleSpec.Builder;
import org.jboss.modules.filter.PathFilters;

public abstract class BaseModuleSpecProvider implements ModuleSpecProvider
{
   @Override
   public ModuleSpec get(ModuleLoader loader, ModuleIdentifier id)
   {
      if (getId().equals(id))
      {
         Builder builder = ModuleSpec.build(id);
         builder.addDependency(DependencySpec.createClassLoaderDependencySpec(PathFilters.acceptAll(),
                  PathFilters.acceptAll(), ClassLoader.getSystemClassLoader(), getPaths()));

         configure(loader, builder);

         return builder.create();
      }
      return null;
   }

   protected void configure(ModuleLoader loader, Builder builder)
   {
   }

   protected abstract ModuleIdentifier getId();

   protected abstract Set<String> getPaths();
}
