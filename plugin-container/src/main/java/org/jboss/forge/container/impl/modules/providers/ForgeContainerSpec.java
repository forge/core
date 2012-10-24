package org.jboss.forge.container.impl.modules.providers;

import java.util.HashSet;
import java.util.Set;

import org.jboss.modules.ModuleIdentifier;

public class ForgeContainerSpec extends BaseModuleSpecProvider
{
   public static final ModuleIdentifier ID = ModuleIdentifier.create("org.jboss.forge.container.impl");

   public static Set<String> paths = new HashSet<String>();

   static
   {
      paths.add("org/jboss/forge/container/impl");
      paths.add("org/jboss/forge/container/impl/event");
      paths.add("org/jboss/forge/container/impl/exception");
      paths.add("org/jboss/forge/container/impl/modules");
      paths.add("org/jboss/forge/container/impl/modules/providers");
      paths.add("org/jboss/forge/container/impl/services");
      paths.add("org/jboss/forge/container/impl/util");
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
