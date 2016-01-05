package test.org.jboss.forge.addon.facets.constraints;

import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.facets.constraints.FacetConstraintType;

import test.org.jboss.forge.addon.facets.factory.MockFacet;

@FacetConstraint(value = FacetP.class, type = FacetConstraintType.REQUIRED)
@FacetConstraint(value = FacetO.class, type = FacetConstraintType.OPTIONAL)
public class FacetN extends MockFacet
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
