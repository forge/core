/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.converter;

import org.jboss.forge.container.services.Exported;

/**
 * Stores converter objects
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
@Exported
public interface StringConverterRegistry
{
   /**
    * Add a plain converter to this registry. The convertible sourceType/targetType pair is specified explicitly. Allows
    * for a Converter to be reused for multiple distinct pairs without having to create a Converter class for each pair.
    */
   <T> void addConverter(Class<T> type, StringConverter<T> converter);

   /**
    * Remove any converters from sourceType to targetType.
    *
    * @param sourceType the source type
    * @param targetType the target type
    */
   void removeConverter(Class<?> type);

   /**
    * Returns a StringConverter for the supplied source and target
    *
    * @param type
    * @return
    */
   <T> StringConverter<T> getConverter(Class<T> type);
}
