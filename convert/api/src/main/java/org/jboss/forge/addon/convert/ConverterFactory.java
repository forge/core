/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.convert;


/**
 * Creates {@link Converter} objects
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public interface ConverterFactory
{
   /**
    * Returns a converter for the supplied source and target
    * 
    * @param source
    * @param target
    * @return
    */
   <SOURCE, TARGET> Converter<SOURCE, TARGET> getConverter(Class<SOURCE> source, Class<TARGET> target);
}
