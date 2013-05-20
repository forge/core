/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.convert;

import org.jboss.forge.furnace.services.Exported;

@Exported
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
