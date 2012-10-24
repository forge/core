package org.jboss.forge.container.impl.modules;

import org.jboss.modules.ModuleIdentifier;
import org.jboss.modules.ModuleSpec;

public interface ModuleSpecProvider
{
   ModuleSpec get(ModuleIdentifier id);
}
