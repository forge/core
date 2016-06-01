/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.convert;

/**
 * Base class for {@link Converter} implementations
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public abstract class AbstractConverter<SOURCETYPE, TARGETTYPE> implements Converter<SOURCETYPE, TARGETTYPE>
{

   private Class<SOURCETYPE> sourceType;
   private Class<TARGETTYPE> targetType;

   public AbstractConverter(Class<SOURCETYPE> sourceType, Class<TARGETTYPE> targetType)
   {
      super();
      this.sourceType = sourceType;
      this.targetType = targetType;
   }

   public Class<SOURCETYPE> getSourceType()
   {
      return sourceType;
   }

   public Class<TARGETTYPE> getTargetType()
   {
      return targetType;
   }
}
