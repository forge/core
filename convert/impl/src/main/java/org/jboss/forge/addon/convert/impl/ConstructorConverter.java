/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.convert.impl;

import java.lang.reflect.Constructor;

import javax.enterprise.inject.Vetoed;

import org.jboss.forge.addon.convert.AbstractConverter;
import org.jboss.forge.addon.convert.exception.ConversionException;

/**
 * Converter that uses a constructor
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 * @param <SOURCETYPE>
 * @param <TARGETTYPE>
 */

@Vetoed
public class ConstructorConverter<SOURCETYPE, TARGETTYPE> extends AbstractConverter<SOURCETYPE, TARGETTYPE>
{
   private final Constructor<TARGETTYPE> constructor;

   public ConstructorConverter(Class<SOURCETYPE> sourceType, Class<TARGETTYPE> targetType, Constructor<TARGETTYPE> constructor)
   {
      super(sourceType, targetType);
      this.constructor = constructor;
   }

   @Override
   public TARGETTYPE convert(SOURCETYPE source)
   {
      try
      {
         return constructor.newInstance(source);
      }
      catch (Exception e)
      {
         throw new ConversionException("Could not convert [" + source + "] to type [" + getTargetType() + "]", e);
      }
   }
}
