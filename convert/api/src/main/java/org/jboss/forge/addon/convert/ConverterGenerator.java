/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.convert;

import org.jboss.forge.furnace.spi.ServiceRegistry;

/**
 * A {@link ConverterGenerator} is responsible for creating {@link Converter} objects
 * 
 * In order to register a custom {@link Converter} implementation, a {@link ConverterGenerator} must be created and
 * subsequently registered in your addon's {@link ServiceRegistry}.
 * 
 * A {@link ConverterGenerator} should generate only one {@link Converter} type.
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public interface ConverterGenerator
{
   /**
    * If this {@link ConverterGenerator} can handle the conversion between the provided source and target {@link Class}
    * types.
    * 
    * @param source {@link Class} of the object to be converted from
    * @param target {@link Class} of the object to be converted to
    * @return true if this generator can handle this conversion request
    */
   boolean handles(Class<?> source, Class<?> target);

   /**
    * Creates a new {@link Converter} instance for the given source and target {@link Class} types.
    * 
    * @param source {@link Class} of the object to be converted from
    * @param target {@link Class} of the object to be converted to
    * @return the Converter associated with this generator
    */
   Converter<?, ?> generateConverter(Class<?> source, Class<?> target);

   /**
    * @return the {@link Class} type of the converter returned by
    *         {@link ConverterGenerator#generateConverter(Class, Class)}
    */
   Class<? extends Converter<?, ?>> getConverterType();
}
