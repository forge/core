/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.facets;

/**
 * An exception representing the state where a facet was requested, but more than one implementation was found.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class FacetIsAmbiguousException extends RuntimeException
{
   private static final long serialVersionUID = 633736084707564318L;

   public FacetIsAmbiguousException()
   {
   }

   public FacetIsAmbiguousException(final String message)
   {
      super(message);
   }

   public FacetIsAmbiguousException(final Throwable e)
   {
      super(e);
   }

   public FacetIsAmbiguousException(final String message, final Throwable e)
   {
      super(message, e);
   }

}