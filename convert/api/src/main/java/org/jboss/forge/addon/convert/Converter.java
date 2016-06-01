/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.convert;

/**
 * Converts from a given SOURCE_TYPE to TARGET_TYPE
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public interface Converter<SOURCE_TYPE, TARGET_TYPE>
{
   /**
    * Convert the source of type SOURCE_TYPE to target type TARGET_TYPE.
    *
    * @param source the source object to convert, which must be an instance of S
    * @return the converted object, which must be an instance of T
    */
   TARGET_TYPE convert(SOURCE_TYPE source);

}
