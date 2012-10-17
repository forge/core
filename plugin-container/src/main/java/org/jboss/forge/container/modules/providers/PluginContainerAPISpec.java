package org.jboss.forge.container.modules.providers;

import org.jboss.forge.container.modules.ModuleSpecProvider;
import org.jboss.modules.ModuleIdentifier;
import org.jboss.modules.ModuleSpec;

public class PluginContainerAPISpec implements ModuleSpecProvider
{
   public static final ModuleIdentifier ID = ModuleIdentifier.create("org.jboss.forge.container.api");

   @Override
   public ModuleSpec get(ModuleIdentifier id)
   {
      if (ID.equals(id))
      {
      }
      return null;
   }

}
