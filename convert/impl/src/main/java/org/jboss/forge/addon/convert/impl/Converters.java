/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.convert.impl;

import javax.enterprise.inject.Vetoed;

import org.jboss.forge.addon.convert.Converter;


/**
 * Converters for general use
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
@Vetoed
public enum Converters implements Converter<Object, Object>
{
   /**
    * Does nothing
    */
   NOOP
   {
      @Override
      public Object convert(Object source)
      {
         return source;
      }

   }
   ,
   /**
    * Calls the {@link Object#toString()} method of the source object
    */
   TO_STRING
   {
      @Override
      public Object convert(Object source)
      {
         return (source != null) ? source.toString() : null;
      }
   };
}
