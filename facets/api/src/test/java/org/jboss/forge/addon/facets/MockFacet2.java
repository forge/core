package org.jboss.forge.addon.facets;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class MockFacet2 extends MockFacet
{
   public MockFacet2(MockFaceted origin)
   {
      super(origin);
   }

   @Override
   public boolean isSupported()
   {
      return false;
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
