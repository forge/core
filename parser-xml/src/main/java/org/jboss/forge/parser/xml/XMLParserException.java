/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.parser.xml;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class XMLParserException extends RuntimeException
{
   private static final long serialVersionUID = -4512252690684442975L;

   public XMLParserException()
   {
      super();
   }

   public XMLParserException(final String message, final Throwable e)
   {
      super(message, e);
   }

   public XMLParserException(final String message)
   {
      super(message);
   }

   public XMLParserException(final Throwable e)
   {
      super(e);
   }

}
