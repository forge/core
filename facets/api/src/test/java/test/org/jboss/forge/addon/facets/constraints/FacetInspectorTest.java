/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package test.org.jboss.forge.addon.facets.constraints;

import java.util.Set;

import org.jboss.forge.addon.facets.AbstractFacet;
import org.jboss.forge.addon.facets.AbstractFaceted;
import org.jboss.forge.addon.facets.constraints.FacetInspector;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class FacetInspectorTest
{
   @Test
   public void testEmptyFacetConstraintsAnnotation()
   {
      Set<Class<FacetZ>> facets = FacetInspector.getAllRelatedFacets(FacetZ.class);
      Assert.assertTrue("Facet list should have been empty", facets.isEmpty());
   }

   public class FacetZ extends AbstractFacet<AbstractFaceted<FacetZ>>
   {
      @Override
      public boolean install()
      {
         return false;
      }

      @Override
      public boolean isInstalled()
      {
         return false;
      }
   }
}
