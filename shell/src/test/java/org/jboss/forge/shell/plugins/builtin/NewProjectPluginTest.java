package org.jboss.forge.shell.plugins.builtin;

import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.jboss.forge.maven.facets.MavenPackagingFacet;
import org.jboss.forge.project.packaging.PackagingType;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Test;

public class NewProjectPluginTest extends AbstractShellTest
{
   @Test()
   public void testNewProjectTypeEar() throws Exception
   {
      initializeJavaProject();
      queueInputLines("Y", "Y");
      getShell().execute("new-project --named nptest --topLevelPackage de.test --type ear");
      assertTrue(PackagingType.EAR == getPackaginFacet());
   }

   private PackagingType getPackaginFacet()
   {
      Collection<?> facets = getProject().getFacets();
      for (Object facet : facets)
      {
         if (facet instanceof MavenPackagingFacet)
         {
            return ((MavenPackagingFacet) facet).getPackagingType();
         }
      }
      return null;
   }
}
