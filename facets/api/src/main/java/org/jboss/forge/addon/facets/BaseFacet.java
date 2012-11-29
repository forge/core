/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.facets;

/**
 * A base convenience {@link Facet} abstract class.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>, <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public abstract class BaseFacet implements Facet
{
   protected Faceted facetedInstance;

   @Override
   public Faceted getFaceted()
   {
      return this.facetedInstance;
   }

   @Override
   public void setFaceted(Faceted faceted)
   {
      this.facetedInstance = faceted;
   }

   @Override
   public boolean uninstall()
   {
      return false;
   }

   /**
    * Facet instances are the same if they are registered to the same project.
    */
   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + ((facetedInstance == null) ? 0 : facetedInstance.hashCode());
      return result;
   }

   @Override
   public boolean equals(final Object obj)
   {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      BaseFacet other = (BaseFacet) obj;
      if (facetedInstance == null)
      {
         if (other.facetedInstance != null)
            return false;
      }
      else if (!facetedInstance.equals(other.facetedInstance))
         return false;
      return true;
   }

}