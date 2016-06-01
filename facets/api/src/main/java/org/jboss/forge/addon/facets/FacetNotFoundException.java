/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.facets;

/**
 * An exception representing the state where a facet was requested, but was not available.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class FacetNotFoundException extends RuntimeException
{
   private static final long serialVersionUID = 633736084707564318L;

   public FacetNotFoundException()
   {
      super("No message");
   }

   public FacetNotFoundException(final String message)
   {
      super(message);
   }

   public FacetNotFoundException(final String message, final Throwable e)
   {
      super(message, e);
   }

}