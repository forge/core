/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.convert;

import org.jboss.forge.furnace.services.Exported;

/**
 * A {@link ConverterGenerator} is responsible for creating {@link Converter} objects
 * 
 * Custom converterts are required to implement this interface.
 * 
 * A {@link ConverterGenerator} should generate only one type of {@link Converter}
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
@Exported
public interface ConverterGenerator
{
   /**
    * If this {@link ConverterGenerator} can handle the conversion between the provided source and target parameters
    * 
    * @param source {@link Class} of the object to be converted from
    * @param target {@link Class} of the object to be converted to
    * @return true if this generator can handle this conversion request
    */
   boolean handles(Class<?> source, Class<?> target);

   /**
    * Creates a new {@link Converter} instance
    * 
    * @param source {@link Class} of the object to be converted from
    * @param target {@link Class} of the object to be converted to
    * @return the Converter associated with this generator
    */
   Converter<?, ?> generateConverter(Class<?> source, Class<?> target);

   /**
    * @return the type of the converter returned by {@link ConverterGenerator#generateConverter(Class, Class)}
    */
   Class<? extends Converter<?, ?>> getConverterType();
}
