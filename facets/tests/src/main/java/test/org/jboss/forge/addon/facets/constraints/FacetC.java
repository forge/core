package test.org.jboss.forge.addon.facets.constraints;

import test.org.jboss.forge.addon.facets.factory.MockFacet;

public class FacetC extends MockFacet
{
   @Override
   public boolean install()
   {
      return true;
   }

   @Override
   public boolean isInstalled()
   {
      return getFaceted().hasFacet(getClass());
   }

}
