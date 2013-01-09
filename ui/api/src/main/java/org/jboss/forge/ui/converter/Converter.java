/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui.converter;

import org.jboss.forge.container.services.Remote;

@Remote
public interface Converter<S, T>
{
   /**
    * Convert the source of type S to target type T.
    * 
    * @param source the source object to convert, which must be an instance of S
    * @return the converted object, which must be an instance of T
    * @throws IllegalArgumentException if the source could not be converted to the desired target type
    */
   T convert(S source) throws Exception;
}
