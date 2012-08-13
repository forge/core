/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.project.facets;

/**
 * Thrown when a user aborts installation of a given Facet.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class FacetActionAborted extends RuntimeException
{
   private static final long serialVersionUID = -1271812418795623520L;

   public FacetActionAborted()
   {
      super();
   }

   public FacetActionAborted(String message, Throwable cause)
   {
      super(message, cause);
   }

   public FacetActionAborted(String message)
   {
      super(message);
   }

   public FacetActionAborted(Throwable cause)
   {
      super(cause);
   }

}
