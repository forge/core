package org.jboss.forge.furnace.modules.providers;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.jboss.modules.DependencySpec;
import org.jboss.modules.ModuleIdentifier;
import org.jboss.modules.ModuleLoader;
import org.jboss.modules.ModuleSpec.Builder;
import org.jboss.modules.filter.PathFilters;

public class WeldClasspathSpec extends AbstractModuleSpecProvider
{
   public static final ModuleIdentifier ID = ModuleIdentifier.create("org.jboss.weld");

   public static Set<String> paths = new HashSet<String>();

   static
   {
      paths.add("META-INF/services");
   }

   @Override
   protected void configure(ModuleLoader loader, Builder builder)
   {
      builder.addDependency(DependencySpec.createSystemDependencySpec(
               PathFilters.acceptAll(),
               PathFilters.any(Arrays.asList(
                        PathFilters.isChildOf("javax"),
                        PathFilters.is("org/jboss/weld"),
                        PathFilters.isChildOf("org/jboss/weld"))),
               systemPaths));
   }

   @Override
   protected ModuleIdentifier getId()
   {
      return ID;
   }

   @Override
   protected Set<String> getPaths()
   {
      return paths;
   }

}
