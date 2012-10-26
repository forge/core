package org.jboss.forge.container.modules.providers;

import java.util.HashSet;
import java.util.Set;

import org.jboss.modules.ModuleIdentifier;

public class SunMiscClasspathSpec extends BaseModuleSpecProvider
{
   public static final ModuleIdentifier ID = ModuleIdentifier.create("sun.jdk");

   public static Set<String> paths = new HashSet<String>();

   static
   {
      paths.add("sun/misc");
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
