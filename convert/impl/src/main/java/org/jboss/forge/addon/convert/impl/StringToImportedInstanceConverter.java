/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.convert.impl;

import javax.enterprise.inject.Vetoed;

import org.jboss.forge.addon.convert.AbstractConverter;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.services.Imported;

/**
 * Lookups in the {@link AddonRegistry} an instance based on the {@link Object#toString()} method
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
@Vetoed
public class StringToImportedInstanceConverter<TARGETTYPE> extends AbstractConverter<String, TARGETTYPE>
{
   private AddonRegistry addonRegistry;

   public StringToImportedInstanceConverter(Class<TARGETTYPE> targetType, AddonRegistry addonRegistry)
   {
      super(String.class, targetType);
      this.addonRegistry = addonRegistry;
   }

   @Override
   public TARGETTYPE convert(String source)
   {
      Imported<TARGETTYPE> instances = addonRegistry.getServices(getTargetType());
      for (TARGETTYPE instance : instances)
      {
         if (source.equals(instance.toString()))
         {
            return instance;
         }
      }
      return null;
   }

}
