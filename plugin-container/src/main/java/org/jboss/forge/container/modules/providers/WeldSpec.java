package org.jboss.forge.container.modules.providers;

import org.jboss.forge.container.modules.ModuleSpecProvider;
import org.jboss.modules.ModuleIdentifier;
import org.jboss.modules.ModuleSpec;

public class WeldSpec implements ModuleSpecProvider
{
   public static final ModuleIdentifier ID = ModuleIdentifier.create("weld");

   @Override
   public ModuleSpec get(ModuleIdentifier id)
   {
      if (ID.equals(id))
      {
      }
      return null;
   }

}
