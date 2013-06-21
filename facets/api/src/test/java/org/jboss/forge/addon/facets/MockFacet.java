package org.jboss.forge.addon.facets;

import org.jboss.forge.addon.facets.AbstractFacet;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class MockFacet extends AbstractFacet<MockFaceted>
{
   public MockFacet(MockFaceted origin)
   {
      super.setFaceted(origin);
   }

   public boolean isSupported()
   {
      return true;
   }

   private boolean installed = false;

   @Override
   public boolean install()
   {
      return installed = true;
   }

   @Override
   public boolean isInstalled()
   {
      return installed;
   }

   @Override
   public boolean uninstall()
   {
      return !(installed = false);
   }
}
