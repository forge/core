package test.org.jboss.forge.addon.facets.constraints;

import org.jboss.forge.addon.facets.constraints.FacetConstraint;

import test.org.jboss.forge.addon.facets.factory.MockFacet;

@FacetConstraint({ FacetB.class, FacetC.class })
public class FacetB extends MockFacet
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
