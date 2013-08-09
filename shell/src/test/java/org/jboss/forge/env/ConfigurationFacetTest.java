package org.jboss.forge.env;

import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.ConfigurationFacet;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Assert;
import org.junit.Test;

public class ConfigurationFacetTest extends AbstractShellTest
{
   @Test
   public void testProjectScopedConfigurationFacet() throws Exception
   {
      Project project = getProject();
      Assert.assertFalse(project.hasFacet(ConfigurationFacet.class));
      getShell().execute("project install-facet test.config.facet");
      Assert.assertTrue(project.hasFacet(ConfigurationFacet.class));
      Assert.assertTrue(project.hasFacet(ConfigDependentFacet.class));
      Assert.assertNotNull(project.getFacet(ConfigDependentFacet.class).getProjectConfiguration());
   }

}
