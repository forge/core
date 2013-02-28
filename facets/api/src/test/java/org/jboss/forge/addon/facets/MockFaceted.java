package org.jboss.forge.addon.facets;

import org.jboss.forge.facets.BaseFaceted;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class MockFaceted extends BaseFaceted<MockFacet>
{
   @Override
   public boolean supports(MockFacet facet)
   {
      return facet.isSupported();
   }
}
