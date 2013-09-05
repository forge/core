package test.org.jboss.forge.addon.facets.constraints;

import org.jboss.forge.addon.facets.constraints.FacetConstraint;

import test.org.jboss.forge.addon.facets.factory.MockFacet;

@FacetConstraint(FacetE.class)
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
