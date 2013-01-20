/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.convert;

public interface Converter<S, T>
{
   /**
    * Get the source {@link Class} type this {@link Converter} accepts in input.
    */
   Class<S> getSourceType();

   /**
    * Get the target {@link Class} type this {@link Converter} produces.
    */
   Class<T> getTargetType();

   /**
    * Convert the source of type S to target type T.
    * 
    * @param source the source object to convert, which must be an instance of S
    * @return the converted object, which must be an instance of T
    */
   T convert(S source);
}
