package org.jboss.forge.spec.jsf;

import junit.framework.Assert;

import org.jboss.forge.project.Project;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Test;

public class FacesPluginTest extends AbstractShellTest
{

   @Test
   public void testFacesConfig() throws Exception
   {
      getShell().setOutputStream(System.out);
      Project project = initializeJavaProject();
      queueInputLines("Y", "", "Y", "Y");
      getShell().execute("faces setup");
      Assert.assertTrue(project.getProjectRoot().getChild("src/main/webapp/WEB-INF/web.xml").exists());
   }

}