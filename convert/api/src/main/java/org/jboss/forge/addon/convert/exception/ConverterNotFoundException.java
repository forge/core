/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.convert.exception;

/**
 * Thrown when a converter is not found in the registry
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public class ConverterNotFoundException extends ConversionException
{
   private static final long serialVersionUID = 1L;

   public ConverterNotFoundException()
   {
      super("No message");
   }

   public ConverterNotFoundException(Class<?> source, Class<?> target)
   {
      super("No converter found from " + source.getName() + " to " + target.getName());
   }

   public ConverterNotFoundException(String message, Throwable e)
   {
      super(message, e);
   }

   public ConverterNotFoundException(String message)
   {
      super(message);
   }
}
