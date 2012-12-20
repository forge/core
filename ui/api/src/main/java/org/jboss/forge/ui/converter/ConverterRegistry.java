/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui.converter;

/**
 * Stores converter objects
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public interface ConverterRegistry
{
   /**
    * Add a plain converter to this registry. The convertible sourceType/targetType pair is specified explicitly. Allows
    * for a Converter to be reused for multiple distinct pairs without having to create a Converter class for each pair.
    */
   <S, T> void addConverter(Class<S> sourceType, Class<T> targetType, Converter<S, T> converter);

   /**
    * Remove any converters from sourceType to targetType.
    *
    * @param sourceType the source type
    * @param targetType the target type
    */
   void removeConverter(Class<?> sourceType, Class<?> targetType);

   /**
    * Returns a converter for the supplied source and target
    *
    * @param source
    * @param target
    * @return
    */
   <S, T> Converter<S, T> getConverter(Class<S> source, Class<T> target);
}
