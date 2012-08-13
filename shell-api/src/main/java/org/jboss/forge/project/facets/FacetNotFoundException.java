/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.project.facets;

import org.jboss.forge.project.ProjectModelException;

/**
 * An exception representing the state where a project facet was requested, but was not available.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class FacetNotFoundException extends ProjectModelException
{
   private static final long serialVersionUID = 633736084707564318L;

   public FacetNotFoundException()
   {
   }

   public FacetNotFoundException(final String message)
   {
      super(message);
   }

   public FacetNotFoundException(final Throwable e)
   {
      super(e);
   }

   public FacetNotFoundException(final String message, final Throwable e)
   {
      super(message, e);
   }

}
