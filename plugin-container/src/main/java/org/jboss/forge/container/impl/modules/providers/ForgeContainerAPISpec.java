package org.jboss.forge.container.impl.modules.providers;

import java.util.HashSet;
import java.util.Set;

import org.jboss.modules.ModuleIdentifier;

public class ForgeContainerAPISpec extends BaseModuleSpecProvider
{
   public static final ModuleIdentifier ID = ModuleIdentifier.create("org.jboss.forge.container.impl.api");

   public static Set<String> paths = new HashSet<String>();

   static
   {
      paths.add("org/jboss/forge/container");
      paths.add("org/jboss/forge/container/event");
      paths.add("org/jboss/forge/container/services");
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
