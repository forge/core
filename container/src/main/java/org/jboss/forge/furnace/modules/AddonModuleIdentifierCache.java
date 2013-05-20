/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.furnace.modules;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.modules.ModuleIdentifier;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
class AddonModuleIdentifierCache
{
   private Map<AddonId, ModuleIdentifier> map = new HashMap<AddonId, ModuleIdentifier>();

   public void clear(AddonId addonId)
   {
      map.remove(addonId);
   }

   public ModuleIdentifier getModuleId(AddonId addonId)
   {
      if (!map.containsKey(addonId))
         map.put(addonId, ModuleIdentifier.fromString(toModuleId(addonId) + "_" + UUID.randomUUID().toString()));
      return map.get(addonId);
   }

   private String toModuleId(AddonId addonId)
   {
      return addonId.getName().replaceAll(":", ".") + ":" + addonId.getVersion();
   }

}
