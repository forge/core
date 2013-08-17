package org.jboss.forge.addon.facets.requirements;

import org.jboss.forge.addon.facets.MockFacet;
import org.jboss.forge.addon.facets.constraints.RequiresFacet;

@RequiresFacet(FacetD.class)
public class FacetE extends MockFacet
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
