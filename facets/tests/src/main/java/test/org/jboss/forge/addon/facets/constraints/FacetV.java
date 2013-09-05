package test.org.jboss.forge.addon.facets.constraints;

import test.org.jboss.forge.addon.facets.factory.MockFacet;

public class FacetV extends MockFacet
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
