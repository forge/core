/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.command.convert;

import java.net.MalformedURLException;
import java.net.URL;

import org.mvel2.ConversionHandler;

public class URLConverter implements ConversionHandler
{

   @SuppressWarnings("rawtypes")
   @Override
   public boolean canConvertFrom(Class type)
   {
      return String.class.isAssignableFrom(type);
   }

   @Override
   public Object convertFrom(Object value)
   {
      try
      {
         return new URL((String) value);
      }
      catch (MalformedURLException e)
      {
         throw new IllegalArgumentException("Could not convert [" + value + "] to type java.net.URL");
      }
   }

}
