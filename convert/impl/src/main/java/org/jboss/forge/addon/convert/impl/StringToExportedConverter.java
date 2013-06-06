/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.convert.impl;

import java.util.Set;

import javax.enterprise.inject.Vetoed;

import org.jboss.forge.addon.convert.AbstractConverter;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.services.Exported;
import org.jboss.forge.furnace.services.ExportedInstance;

/**
 * Lookups in the {@link AddonRegistry} an {@link Exported} instance based on the {@link Object#toString()} method
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 * @param <TARGETTYPE> a type declaring the {@link Exported} annotation
 */
@Vetoed
public class StringToExportedConverter<TARGETTYPE> extends AbstractConverter<String, TARGETTYPE>
{
   private AddonRegistry addonRegistry;

   public StringToExportedConverter(Class<TARGETTYPE> targetType, AddonRegistry addonRegistry)
   {
      super(String.class, targetType);
      this.addonRegistry = addonRegistry;
   }

   @Override
   public TARGETTYPE convert(String source)
   {
      Set<ExportedInstance<TARGETTYPE>> exportedInstances = addonRegistry.getExportedInstances(getTargetType());
      for (ExportedInstance<TARGETTYPE> exportedInstance : exportedInstances)
      {
         TARGETTYPE targetObj = null;
         try
         {
            targetObj = exportedInstance.get();
            if (source.equals(targetObj.toString()))
            {
               return targetObj;
            }
         }
         finally
         {
            exportedInstance.release(targetObj);
         }
      }
      return null;
   }

}
