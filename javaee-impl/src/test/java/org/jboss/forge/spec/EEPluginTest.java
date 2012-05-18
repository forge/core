package org.jboss.forge.spec;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.project.Project;
import org.jboss.forge.spec.javaee.EJBFacet;
import org.jboss.forge.spec.javaee.JTAFacet;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @Author Paul Bakker - paul.bakker@luminis.eu
 */
@RunWith(Arquillian.class)
public class EEPluginTest extends AbstractShellTest
{
   @Test
   public void testSetupEJB() throws Exception
   {
      Project project = initializeJavaProject();

      assertFalse(project.hasFacet(EJBFacet.class));
      queueInputLines("");
      getShell().execute("setup ejb");
      assertTrue(project.hasFacet(EJBFacet.class));
   }

   @Test
   public void testSetupJTA() throws Exception
   {
      Project project = initializeJavaProject();

      assertFalse(project.hasFacet(JTAFacet.class));
      queueInputLines("");
      getShell().execute("setup jta");
      assertTrue(project.hasFacet(JTAFacet.class));
   }
}
