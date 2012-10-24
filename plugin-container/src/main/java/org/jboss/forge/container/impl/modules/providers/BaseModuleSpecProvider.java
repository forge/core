package org.jboss.forge.container.impl.modules.providers;

import java.util.Set;

import org.jboss.forge.container.impl.modules.ModuleSpecProvider;
import org.jboss.modules.DependencySpec;
import org.jboss.modules.ModuleIdentifier;
import org.jboss.modules.ModuleSpec;
import org.jboss.modules.ModuleSpec.Builder;

public abstract class BaseModuleSpecProvider implements ModuleSpecProvider
{
   @Override
   public ModuleSpec get(ModuleIdentifier id)
   {
      if (getId().equals(id))
      {
         Builder builder = ModuleSpec.build(id);
         builder.addDependency(DependencySpec.createClassLoaderDependencySpec(
                  ClassLoader.getSystemClassLoader(), getPaths(), true));

         configure(builder);

         return builder.create();
      }
      return null;
   }

   protected void configure(Builder builder)
   {
   }

   protected abstract ModuleIdentifier getId();

   protected abstract Set<String> getPaths();
}
