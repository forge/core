package org.jboss.forge.addon.facets;

import org.jboss.forge.addon.facets.AbstractFaceted;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class MockFaceted extends AbstractFaceted<MockFacet>
{
   @Override
   public boolean supports(MockFacet facet)
   {
      return facet.isSupported();
   }
}
