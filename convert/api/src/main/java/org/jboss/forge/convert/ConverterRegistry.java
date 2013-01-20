/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.convert;


/**
 * Stores converter objects
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public interface ConverterRegistry
{
   /**
    * Returns a converter for the supplied source and target
    * 
    * @param source
    * @param target
    * @return
    */
   <S, T> Converter<S, T> getConverter(Class<S> source, Class<T> target);
}
