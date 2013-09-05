package test.org.jboss.forge.addon.facets.constraints;

import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.facets.constraints.FacetConstraintType;
import org.jboss.forge.addon.facets.constraints.FacetConstraints;

import test.org.jboss.forge.addon.facets.factory.MockFacet;

@FacetConstraints({
         @FacetConstraint(value = { FacetO.class, FacetP.class }, type = FacetConstraintType.OPTIONAL)
})
public class FacetM extends MockFacet
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
