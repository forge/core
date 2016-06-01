/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.convert.exception;

import org.jboss.forge.addon.convert.Converter;

/**
 * Thrown when a conversion using a {@link Converter} fails
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ConversionException extends RuntimeException
{
   private static final long serialVersionUID = -1744577611317933091L;

   public ConversionException()
   {
      super("No message");
   }

   public ConversionException(String message, Throwable e)
   {
      super(message, e);
   }

   public ConversionException(String message)
   {
      super(message);
   }

}
