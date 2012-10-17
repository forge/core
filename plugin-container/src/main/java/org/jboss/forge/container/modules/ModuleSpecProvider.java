package org.jboss.forge.container.modules;

import org.jboss.modules.ModuleIdentifier;
import org.jboss.modules.ModuleSpec;

public interface ModuleSpecProvider
{
   ModuleSpec get(ModuleIdentifier id);
}
