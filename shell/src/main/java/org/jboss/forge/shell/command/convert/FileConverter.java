/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.command.convert;

import java.io.File;

import org.mvel2.ConversionHandler;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class FileConverter implements ConversionHandler
{
   @Override
   public Object convertFrom(final Object in)
   {
      return new File(in.toString());
   }

   @Override
   @SuppressWarnings("rawtypes")
   public boolean canConvertFrom(final Class type)
   {
      return String.class.isAssignableFrom(type);
   }
}
