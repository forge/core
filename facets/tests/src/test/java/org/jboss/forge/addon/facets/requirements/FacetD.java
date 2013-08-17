package org.jboss.forge.addon.facets.requirements;

import org.jboss.forge.addon.facets.MockFacet;
import org.jboss.forge.addon.facets.constraints.RequiresFacet;

@RequiresFacet(FacetE.class)
public class FacetD extends MockFacet
{
   @Override
   public boolean install()
   {
      return true;
   }

   @Override
   public boolean isInstalled()
   {
      return true;
   }

}
